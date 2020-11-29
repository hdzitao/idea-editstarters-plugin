package hdzi.editstarters.springboot.initializr

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.intellij.util.io.HttpRequests
import hdzi.editstarters.buildsystem.ProjectDependency
import hdzi.editstarters.ui.ShowErrorException

/**
 * Created by taojinhou on 2018/12/21.
 */
class SpringInitializr(url: String, bootVersion: String) {
    val modulesMap = linkedMapOf<String, List<StarterInfo>>()
    private val pointMap = hashMapOf<String, StarterInfo>()
    private val gson = Gson()
    var version: InitializrVersion
    val existStarters = linkedSetOf<StarterInfo>()
    val currentVersionID: String

    init {
        try {
            // 请求initurl
            val baseInfoJSON = HttpRequests.request(url).accept("application/json").connect {
                gson.fromJson(it.readString(null), JsonObject::class.java)
            }
            this.version = gson.fromJson(baseInfoJSON.getAsJsonObject("bootVersion"), InitializrVersion::class.java)
            this.currentVersionID = bootVersion.versionNum()
            val dependenciesUrl = parseDependenciesUrl(baseInfoJSON, this.currentVersionID)
            val depsJSON = HttpRequests.request(dependenciesUrl).connect {
                gson.fromJson(it.readString(null), JsonObject::class.java)
            }
            parseDependencies(baseInfoJSON, depsJSON)
        } catch (ignore: HttpRequests.HttpStatusException) {
            // start.spring.io不支持版本返回404
            throw ShowErrorException("Request failure! Your spring boot version may not be supported, please confirm.")
        } catch (ignore: JsonSyntaxException) {
            // json解析失败
            throw ShowErrorException("Request failure! JSON syntax error for response, please confirm.")
        }
    }

    fun addExistsStarter(depend: ProjectDependency) {
        val starterInfo = this.pointMap[depend.point]
        if (starterInfo != null) {
            starterInfo.exist = true
            this.existStarters.add(starterInfo)
        }
    }

    private fun parseDependenciesUrl(json: JsonObject, version: String): String =
        json.getAsJsonObject("_links")
            .getAsJsonObject("dependencies")
            .get("href").asString
            .replace("{?bootVersion}", "?bootVersion=$version")

    private fun parseDependencies(baseInfoJSON: JsonObject, depJSON: JsonObject) {
        // 设置仓库信息的id
        val depResponse = gson.fromJson(depJSON, InitializrResponse::class.java)
        depResponse.repositories.forEach { (id, repository) -> repository.id = id }

        val modulesJSON = baseInfoJSON.getAsJsonObject("dependencies").getAsJsonArray("values")
        for (moduleEle in modulesJSON) {
            val module = moduleEle.asJsonObject

            val dependenciesJSON = module.getAsJsonArray("values")
            val dependencies = ArrayList<StarterInfo>(dependenciesJSON.size())
            for (depEle in dependenciesJSON) {
                val starterInfo = gson.fromJson(depEle.asJsonObject, StarterInfo::class.java)
                val dependency = depResponse.dependencies[starterInfo.id] ?: continue
                starterInfo.groupId = dependency.groupId
                starterInfo.artifactId = dependency.artifactId
                starterInfo.version = dependency.version
                starterInfo.scope = dependency.scope.toScopeType()
                val bom = depResponse.boms[dependency.bom]
                if (bom != null) {
                    starterInfo.bom = bom
                    bom.repositories.forEach { rid -> starterInfo.addRepository(depResponse.repositories[rid]) }
                }

                this.pointMap[starterInfo.point] = starterInfo

                dependencies.add(starterInfo)
            }

            this.modulesMap[module.get("name").asString] = dependencies
        }
    }

    private fun String.versionNum() = this.replace("""^(\d+\.\d+\.\d+).*$""".toRegex(), "$1")

    private fun String.toScopeType() = DependencyScope.values().find { this == it.scope } ?: DependencyScope.COMPILE
}
