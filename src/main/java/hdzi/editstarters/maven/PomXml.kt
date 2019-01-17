package hdzi.editstarters.maven

import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import hdzi.editstarters.ProjectFile
import hdzi.editstarters.bean.StarterInfo
import hdzi.editstarters.bean.initializr.InitializrBom
import hdzi.editstarters.bean.initializr.InitializrRepository
import hdzi.editstarters.bean.project.ProjectBom
import hdzi.editstarters.bean.project.ProjectDependency
import hdzi.editstarters.bean.project.ProjectRepository
import org.apache.commons.lang.StringUtils

/**
 * Created by taojinhou on 2018/12/24.
 */
class PomXml(file: XmlFile) : ProjectFile<XmlTag>() {
    /**
     * 根标签
     */
    private val rootTag: XmlTag = file.document!!.rootTag!!

    override fun getOrCreateDependenciesTag(): XmlTag = getOrCreateXmlTag(this.rootTag, "dependencies")

    override fun findAllDependencies(dependenciesTag: XmlTag): Sequence<ProjectDependency> =
        dependenciesTag.findSubTags("dependency").asSequence()
            .map { ProjectDependency(getTagText(it, "groupId"), getTagText(it, "artifactId"), it) }

    override fun createDependencyTag(dependenciesTag: XmlTag, info: StarterInfo) {
        val dependency = createSubTag(dependenciesTag as XmlTag, "dependency")
        addSubTagWithTextBody(dependency, "groupId", info.groupId)
        addSubTagWithTextBody(dependency, "artifactId", info.artifactId)
        if ("compile" != info.scope) {
            addSubTagWithTextBody(dependency, "scope", info.scope)
        }
        addSubTagWithTextBody(dependency, "version", info.version)
    }

    override fun getOrCreateBomTag(): XmlTag =
        getOrCreateXmlTag(getOrCreateXmlTag(this.rootTag, "dependencyManagement"), "dependencies")


    override fun findAllBom(bomTag: XmlTag): Sequence<ProjectBom> =
        bomTag.findSubTags("dependency").asSequence()
            .map { ProjectBom(getTagText(it, "groupId"), getTagText(it, "artifactId")) }

    override fun createBomTag(bomTag: XmlTag, bom: InitializrBom) {
        val dependencyTag = createSubTag(bomTag, "dependency")
        addSubTagWithTextBody(dependencyTag, "groupId", bom.groupId)
        addSubTagWithTextBody(dependencyTag, "artifactId", bom.artifactId)
        addSubTagWithTextBody(dependencyTag, "version", bom.version)
        addSubTagWithTextBody(dependencyTag, "type", "pom")
        addSubTagWithTextBody(dependencyTag, "scope", "import")
    }

    override fun getOrCreateRepositoriesTag(): XmlTag = getOrCreateXmlTag(this.rootTag, "repositories")

    override fun findAllRepositories(repositoriesTag: XmlTag): Sequence<ProjectRepository> =
        repositoriesTag.findSubTags("repository").asSequence()
            .map { ProjectRepository(getTagText(it, "url")) }

    override fun createRepositoriesTag(repositoriesTag: XmlTag, repository: InitializrRepository) {
        val repositoryTag = createSubTag(repositoriesTag as XmlTag, "repository")
        addSubTagWithTextBody(repositoryTag, "id", repository.id)
        addSubTagWithTextBody(repositoryTag, "name", repository.name)
        addSubTagWithTextBody(repositoryTag, "url", repository.url)
        if (repository.snapshotEnabled != null) {
            val snapshotsTag = createSubTag(repositoryTag, "snapshots")
            addSubTagWithTextBody(snapshotsTag, "enabled", repository.snapshotEnabled!!.toString())
        }
    }

    /**
     * 获取标签里的值
     */
    private fun getTagText(parent: XmlTag, name: String): String =
        parent.findFirstSubTag(name)?.value?.text!!

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
}
