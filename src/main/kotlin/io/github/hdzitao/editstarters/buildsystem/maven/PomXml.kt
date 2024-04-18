package io.github.hdzitao.editstarters.buildsystem.maven

import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import io.github.hdzitao.editstarters.buildsystem.DependencyElement
import io.github.hdzitao.editstarters.buildsystem.DependencyScope
import io.github.hdzitao.editstarters.buildsystem.DependencyScope.Companion.getByScope
import io.github.hdzitao.editstarters.buildsystem.ProjectFile
import io.github.hdzitao.editstarters.dependency.Bom
import io.github.hdzitao.editstarters.dependency.Dependency
import io.github.hdzitao.editstarters.dependency.Repository
import io.github.hdzitao.editstarters.springboot.Starter
import io.github.hdzitao.editstarters.ui.ShowErrorException
import java.util.*
import java.util.stream.Collectors

/**
 * pom.xml文件
 *
 * @version 3.2.0
 */
class PomXml(override val buildFile: XmlFile) : ProjectFile<XmlFile, XmlTag>() {
    companion object {
        // TAG ==================================================================================
        private const val TAG_DEPENDENCY_MANAGEMENT = "dependencyManagement"
        private const val TAG_DEPENDENCIES = "dependencies"
        private const val TAG_DEPENDENCY = "dependency"
        private const val TAG_GROUP_ID = "groupId"
        private const val TAG_ARTIFACT_ID = "artifactId"
        private const val TAG_VERSION = "version"
        private const val TAG_SCOPE = "scope"
        private const val TAG_REPOSITORIES = "repositories"
        private const val TAG_REPOSITORY = "repository"
        // TAG ==================================================================================
    }

    init {
        buildFile.document?.rootTag ?: throw ShowErrorException.internal()
    }

    override fun XmlFile.findOrCreateDependenciesTag(): XmlTag {
        return this.document!!.rootTag!!.getOrCreateXmlTag(TAG_DEPENDENCIES)
    }

    override fun XmlTag.findAllDependencies(): List<Dependency> {
        return findSubTags(TAG_DEPENDENCY)
            .map {
                DependencyElement(it.getTagText(TAG_GROUP_ID), it.getTagText(TAG_ARTIFACT_ID), it)
            }
            .toList()
    }

    override fun XmlTag.createDependencyTag(starter: Starter) {
        val dependency = createSubTag(TAG_DEPENDENCY)
        dependency.addSubTagWithTextBody(TAG_GROUP_ID, starter.groupId)
        dependency.addSubTagWithTextBody(TAG_ARTIFACT_ID, starter.artifactId)
        val scope = getByScope(starter.scope)
        dependency.addSubTagWithTextBody(TAG_SCOPE, scope.resolveMavenScope())
        if (scope == DependencyScope.ANNOTATION_PROCESSOR || scope == DependencyScope.COMPILE_ONLY) {
            dependency.addSubTagWithTextBody("optional", "true")
        }
        dependency.addSubTagWithTextBody(TAG_VERSION, starter.version)
    }

    override fun XmlFile.findOrCreateBomsTag(): XmlTag {
        return this.document!!.rootTag!!
            .getOrCreateXmlTag(TAG_DEPENDENCY_MANAGEMENT)
            .getOrCreateXmlTag(TAG_DEPENDENCIES)
    }


    override fun XmlTag.findAllBoms(): List<Bom> {
        return findSubTags(TAG_DEPENDENCY)
            .map { Bom(it.getTagText(TAG_GROUP_ID), it.getTagText(TAG_ARTIFACT_ID)) }
            .toList()
    }

    override fun XmlTag.createBomTag(bom: Bom) {
        val dependencyTag = createSubTag(TAG_DEPENDENCY)
        dependencyTag.addSubTagWithTextBody(TAG_GROUP_ID, bom.groupId)
        dependencyTag.addSubTagWithTextBody(TAG_ARTIFACT_ID, bom.artifactId)
        dependencyTag.addSubTagWithTextBody(TAG_VERSION, bom.version)
        dependencyTag.addSubTagWithTextBody("type", "pom")
        dependencyTag.addSubTagWithTextBody(TAG_SCOPE, "import")
    }

    override fun XmlFile.findOrCreateRepositoriesTag(): XmlTag {
        return this.document!!.rootTag!!.getOrCreateXmlTag(TAG_REPOSITORIES)
    }

    override fun XmlTag.findAllRepositories(): List<Repository> {
        return Arrays.stream(findSubTags(TAG_REPOSITORY))
            .map { Repository(it.getTagText("url")) }
            .collect(Collectors.toList())
    }

    override fun XmlTag.createRepositoryTag(repository: Repository) {
        val repositoryTag = createSubTag(TAG_REPOSITORY)
        repositoryTag.addSubTagWithTextBody("id", repository.id)
        repositoryTag.addSubTagWithTextBody("name", repository.name)
        repositoryTag.addSubTagWithTextBody("url", repository.url)
        if (repository.isSnapshot) {
            val snapshotsTag = repositoryTag.createSubTag("snapshots")
            snapshotsTag.addSubTagWithTextBody("enabled", true.toString())
        }
    }

    /**
     * 获取标签里的值
     */
    private fun XmlTag.getTagText(name: String): String {
        val subTag = findFirstSubTag(name) ?: return EMPTY
        return subTag.value.text
    }

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
        if (key.isNotBlank() && !value.isNullOrBlank()) {
            addSubTag(createChildTag(key, namespace, value, false), false)
        }
    }

    /**
     * 新建标签
     */
    private fun XmlTag.createSubTag(name: String): XmlTag {
        return addSubTag(createChildTag(name, namespace, null, false), false)
    }

    private fun DependencyScope.resolveMavenScope(): String? {
        return when (this) {
            DependencyScope.PROVIDED -> "provided"
            DependencyScope.RUNTIME -> "runtime"
            DependencyScope.TEST -> "test"
            DependencyScope.ANNOTATION_PROCESSOR, DependencyScope.COMPILE, DependencyScope.COMPILE_ONLY -> null
        }
    }
}
