package hdzi.editstarters.buildsystem.maven

import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import hdzi.editstarters.buildsystem.ProjectBom
import hdzi.editstarters.buildsystem.ProjectDependency
import hdzi.editstarters.buildsystem.ProjectFile
import hdzi.editstarters.buildsystem.ProjectRepository
import hdzi.editstarters.springboot.initializr.InitializrBom
import hdzi.editstarters.springboot.initializr.InitializrRepository
import hdzi.editstarters.springboot.initializr.StarterInfo
import org.apache.commons.lang.StringUtils

/**
 * Created by taojinhou on 2018/12/24.
 */
class PomXml(file: XmlFile) : ProjectFile<XmlTag>() {
    /**
     * 根标签
     */
    private val rootTag: XmlTag = file.document!!.rootTag!!

    override fun getOrCreateDependenciesTag(): XmlTag = this.rootTag.getOrCreateXmlTag("dependencies")

    override fun findAllDependencies(dependenciesTag: XmlTag): Sequence<ProjectDependency> =
        dependenciesTag.findSubTags("dependency").asSequence()
            .map { ProjectDependency(it.getTagText("groupId"), it.getTagText("artifactId"), it) }

    override fun createDependencyTag(dependenciesTag: XmlTag, info: StarterInfo) {
        val dependency = dependenciesTag.createSubTag("dependency")
        dependency.addSubTagWithTextBody("groupId", info.groupId)
        dependency.addSubTagWithTextBody("artifactId", info.artifactId)
        if ("compile" != info.scope) {
            dependency.addSubTagWithTextBody("scope", info.scope)
        }
        dependency.addSubTagWithTextBody("version", info.version)
    }

    override fun getOrCreateBomsTag(): XmlTag =
        this.rootTag.getOrCreateXmlTag("dependencyManagement").getOrCreateXmlTag("dependencies")


    override fun findAllBoms(bomsTag: XmlTag): Sequence<ProjectBom> =
        bomsTag.findSubTags("dependency").asSequence()
            .map { ProjectBom(it.getTagText("groupId"), it.getTagText("artifactId")) }

    override fun createBomTag(bomsTag: XmlTag, bom: InitializrBom) {
        val dependencyTag = bomsTag.createSubTag("dependency")
        dependencyTag.addSubTagWithTextBody("groupId", bom.groupId)
        dependencyTag.addSubTagWithTextBody("artifactId", bom.artifactId)
        dependencyTag.addSubTagWithTextBody("version", bom.version)
        dependencyTag.addSubTagWithTextBody("type", "pom")
        dependencyTag.addSubTagWithTextBody("scope", "import")
    }

    override fun getOrCreateRepositoriesTag(): XmlTag = this.rootTag.getOrCreateXmlTag("repositories")

    override fun findAllRepositories(repositoriesTag: XmlTag): Sequence<ProjectRepository> =
        repositoriesTag.findSubTags("repository").asSequence()
            .map { ProjectRepository(it.getTagText("url")) }

    override fun createRepositoryTag(repositoriesTag: XmlTag, repository: InitializrRepository) {
        val repositoryTag = repositoriesTag.createSubTag("repository")
        repositoryTag.addSubTagWithTextBody("id", repository.id)
        repositoryTag.addSubTagWithTextBody("name", repository.name)
        repositoryTag.addSubTagWithTextBody("url", repository.url)
        if (repository.snapshotEnabled) {
            val snapshotsTag = repositoryTag.createSubTag("snapshots")
            snapshotsTag.addSubTagWithTextBody("enabled", repository.snapshotEnabled.toString())
        }
    }

    /**
     * 获取标签里的值
     */
    private fun XmlTag.getTagText(name: String): String =
        findFirstSubTag(name)?.value?.text!!

    /**
     * 获取或者新建标签
     */
    private fun XmlTag.getOrCreateXmlTag(name: String): XmlTag {
        var subTag = findFirstSubTag(name)
        if (subTag == null) {
            subTag = createSubTag(name)
        }
        return subTag
    }

    /**
     * 添加有内容的标签
     */
    private fun XmlTag.addSubTagWithTextBody(key: String, value: String?) {
        if (StringUtils.isNotEmpty(value) && StringUtils.isNotEmpty(key)) {
            addSubTag(createChildTag(key, this.namespace, value, false), false)
        }
    }

    /**
     * 新建标签
     */
    private fun XmlTag.createSubTag(name: String): XmlTag =
        addSubTag(createChildTag(name, this.namespace, null, false), false)
}
