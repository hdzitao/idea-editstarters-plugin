package hdzi.editstarters.maven

import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import hdzi.editstarters.springboot.ProjectFile
import hdzi.editstarters.springboot.bean.DepResponse
import hdzi.editstarters.springboot.bean.StarterInfo
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang.StringUtils

/**
 * Created by taojinhou on 2018/12/24.
 */
class PomXml(file: XmlFile) : ProjectFile {
    /**
     * 根标签
     */
    private val rootTag: XmlTag = file.document!!.rootTag!!

    /**
     * 删除依赖
     */
    override fun removeDependencies(dependencies: Collection<StarterInfo>) {
        val dependenciesTag = getOrCreateXmlTag(this.rootTag, "dependencies")
        // 取已存在的依赖
        val extdeps = dependenciesTag.findSubTags("dependency")
        // 转化待删除的依赖成字符串形式，方便对比
        val removeDeps = dependencies.map { it.point }.toSet()
        // 遍历存在的依赖，如果待删除的依赖包含它，就删除
        for (extdep in extdeps) {
            if (removeDeps.contains(createPoint(getTagText(extdep, "groupId"), getTagText(extdep, "artifactId")))) {
                extdep.delete()
            }
        }
    }

    /**
     * 添加依赖
     */
    override fun addDependencies(dependencies: Collection<StarterInfo>) {
        val dependenciesTag = getOrCreateXmlTag(this.rootTag, "dependencies")

        for (info in dependencies) {
            val dependency = createSubTag(dependenciesTag, "dependency")
            addSubTagWithTextBody(dependency, "groupId", info.groupId)
            addSubTagWithTextBody(dependency, "artifactId", info.artifactId)
            if ("compile" != info.scope) {
                addSubTagWithTextBody(dependency, "scope", info.scope)
            }
            addSubTagWithTextBody(dependency, "version", info.version)

            if (info.bom != null) {
                addBom(info.bom!!)
            }

            if (CollectionUtils.isNotEmpty(info.repositories)) {
                addRepositories(info.repositories)
            }
        }
    }

    /**
     * 添加仓库信息
     */
    private fun addRepositories(repositories: Set<DepResponse.Repository>) {
        val repositoriesTag = getOrCreateXmlTag(this.rootTag, "repositories")

        val existingRepos = repositoriesTag.findSubTags("repository").asSequence()
            .map {
                createPoint(getTagText(it, "id"), getTagText(it, "url"))
            }.toSet()

        repositories.stream()
            .filter { repo ->
                // 去重
                !existingRepos.contains(createPoint(repo.id!!, repo.url!!))
            }.forEach { repo ->
                // 添加
                val repositoryTag = createSubTag(repositoriesTag, "repository")
                addSubTagWithTextBody(repositoryTag, "id", repo.id)
                addSubTagWithTextBody(repositoryTag, "name", repo.name)
                addSubTagWithTextBody(repositoryTag, "url", repo.url)
                if (repo.snapshotEnabled != null) {
                    val snapshotsTag = createSubTag(repositoryTag, "snapshots")
                    addSubTagWithTextBody(snapshotsTag, "enabled", repo.snapshotEnabled!!.toString())
                }
            }
    }

    /**
     * 获取标签里的值
     */
    private fun getTagText(parent: XmlTag, name: String): String =
        parent.findFirstSubTag(name)?.value?.text!!

    /**
     * 添加bom信息
     */
    private fun addBom(bom: DepResponse.Bom) {
        val dependencyManagementTag = getOrCreateXmlTag(this.rootTag, "dependencyManagement")
        val dependencies = getOrCreateXmlTag(dependencyManagementTag, "dependencies")
        // 去重
        val point = createPoint(bom.groupId!!, bom.artifactId!!)
        for (sub in dependencies.findSubTags("dependency")) {
            if (point == createPoint(getTagText(sub, "groupId"), getTagText(sub, "artifactId"))) {
                return
            }
        }

        val dependencyTag = createSubTag(dependencies, "dependency")
        addSubTagWithTextBody(dependencyTag, "groupId", bom.groupId)
        addSubTagWithTextBody(dependencyTag, "artifactId", bom.artifactId)
        addSubTagWithTextBody(dependencyTag, "version", bom.version)
        addSubTagWithTextBody(dependencyTag, "type", "pom")
        addSubTagWithTextBody(dependencyTag, "scope", "import")
    }

    /**
     * 获取或者新建标签
     */
    private fun getOrCreateXmlTag(parent: XmlTag, name: String): XmlTag {
        var subTag = parent.findFirstSubTag(name)
        if (subTag == null) {
            createSubTag(parent, name)
            subTag = parent.findFirstSubTag(name)
        }
        return subTag!!
    }

    /**
     * 添加有内容的标签
     */
    private fun addSubTagWithTextBody(parent: XmlTag?, key: String, value: String?) {
        if (parent != null && StringUtils.isNotEmpty(value) && StringUtils.isNotEmpty(key)) {
            parent.addSubTag(parent.createChildTag(key, parent.namespace, value, false), false)
        }
    }

    /**
     * 新建标签
     */
    private fun createSubTag(parent: XmlTag, name: String): XmlTag =
        parent.addSubTag(parent.createChildTag(name, parent.namespace, null, false), false)

    /**
     * 生成标识字符串
     */
    private fun createPoint(vararg info: String): String = StringUtils.join(info, ":")

}
