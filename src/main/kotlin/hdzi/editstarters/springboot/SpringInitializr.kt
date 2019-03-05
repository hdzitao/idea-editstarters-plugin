package hdzi.editstarters.springboot

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.intellij.util.io.HttpRequests
import hdzi.editstarters.bean.StarterInfo
import hdzi.editstarters.bean.initializr.InitializrResponse
import hdzi.editstarters.bean.initializr.InitializrVersion
import hdzi.editstarters.bean.project.ProjectDependency

/**
 * Created by taojinhou on 2018/12/21.
 */
class SpringInitializr(url: String, currentVersion: String) {
    val modulesMap = linkedMapOf<String, List<StarterInfo>>()
    val searchDB = linkedMapOf<String, StarterInfo>()
    private val idsMap = hashMapOf<String, StarterInfo>()
    private val anchorsMap = hashMapOf<String, StarterInfo>()
    private val gson = Gson()
    var version: InitializrVersion
    val existStarters = linkedSetOf<StarterInfo>()

    init {
        // 请求initurl
        val baseInfoJSON = HttpRequests.request(url).accept("application/json").connect {
            this.gson.fromJson(it.readString(null), JsonObject::class.java)
        }

        parseSpringBootModules(baseInfoJSON)

        val dependenciesUrl = parseDependenciesUrl(baseInfoJSON, currentVersion)
        val depsJSON = HttpRequests.request(dependenciesUrl).connect {
            this.gson.fromJson(it.readString(null), JsonObject::class.java)
        }

        parseDependencies(depsJSON)

        this.version = this.gson.fromJson(baseInfoJSON.getAsJsonObject("bootVersion"), InitializrVersion::class.java)
    }

    fun addExistsStarter(depend: ProjectDependency) {
        val starterInfo = this.anchorsMap[depend.point]
        if (starterInfo != null) {
            starterInfo.exist = true
            this.existStarters.add(starterInfo)
        }
    }

    private fun parseSpringBootModules(json: JsonObject) {
        val dependenciesJSON = json.getAsJsonObject("dependencies").getAsJsonArray("values")
        for (moduleEle in dependenciesJSON) {
            val module = moduleEle.asJsonObject

            val values = module.getAsJsonArray("values")
            val dependencies = ArrayList<StarterInfo>(values.size())
            for (depEle in values) {
                val baseInfo = depEle.asJsonObject

                val starterInfo = this.gson.fromJson(baseInfo, StarterInfo::class.java)

                this.idsMap[starterInfo.id!!] = starterInfo
                this.searchDB[starterInfo.searchKey] = starterInfo
                dependencies.add(starterInfo)
            }
            this.modulesMap[module.get("name").asString] = dependencies
        }
    }

    private fun parseDependenciesUrl(json: JsonObject, version: String): String {
        return json.getAsJsonObject("_links")
            .getAsJsonObject("dependencies")
            .get("href").asString
            .replace("{?bootVersion}", "?bootVersion=$version")
    }

    private fun parseDependencies(json: JsonObject) {
        // 仓库信息
        val depResponse = this.gson.fromJson(json, InitializrResponse::class.java)
        depResponse.repositories?.forEach { id, repository -> repository.id = id }

        // 合并信息
        for ((id, dependency) in depResponse.dependencies!!) {
            val starterInfo = this.idsMap[id]!!

            starterInfo.groupId = dependency.groupId
            starterInfo.artifactId = dependency.artifactId
            starterInfo.version = dependency.version
            starterInfo.scope = dependency.scope

            starterInfo.addRepository(depResponse.repositories?.get(dependency.repository))
            val bom = depResponse.boms?.get(dependency.bom)
            if (bom != null) {
                starterInfo.bom = bom
                bom.repositories?.forEach { rid -> starterInfo.addRepository(depResponse.repositories?.get(rid)) }
            }

            this.anchorsMap[starterInfo.point] = starterInfo
        }
    }
}
