package hdzi.editstarters.maven

import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import hdzi.editstarters.springboot.bean.DepResponse
import hdzi.editstarters.springboot.bean.StarterInfo
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang.StringUtils
import java.util.stream.Collectors

/**
 * Created by taojinhou on 2018/12/24.
 */
class PomXml(private val file: XmlFile) {
    private val rootTag: XmlTag? = this.file.document!!.rootTag

    fun removeDependencies(dependencies: Collection<StarterInfo>) {
        val dependenciesTag = getOrCreateXmlTag(this.rootTag!!, "dependencies")

        val extdeps = dependenciesTag.findSubTags("dependency")

        val removeDeps = dependencies.stream()
            .map { info -> createPoint(info.groupId!!, info.artifactId!!) }
            .collect(Collectors.toSet())

        for (extdep in extdeps) {
            if (removeDeps.contains(
                    createPoint(
                        getTagText(extdep, "groupId"),
                        getTagText(extdep, "artifactId")
                    )
                )
            ) {

                extdep.delete()
            }
        }
    }


    fun addDependencies(dependencies: Collection<StarterInfo>) {
        val dependenciesTag = getOrCreateXmlTag(this.rootTag!!, "dependencies")

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

    private fun addRepositories(repositories: MutableSet<DepResponse.Repository>) {
        val repositoriesTag = getOrCreateXmlTag(this.rootTag!!, "repositories")
        // 去重
        val iterator = repositories.iterator()
        while (iterator.hasNext()) {
            val repository = iterator.next()
            val point = createPoint(repository.id!!, repository.url!!)
            for (sub in repositoriesTag.findSubTags("repository")) {
                if (point == createPoint(getTagText(sub, "id"), getTagText(sub, "url"))) {
                    iterator.remove()
                }
            }
        }

        for (repository in repositories) {
            val repositoryTag = createSubTag(repositoriesTag, "repository")
            addSubTagWithTextBody(repositoryTag, "id", repository.id)
            addSubTagWithTextBody(repositoryTag, "name", repository.name)
            addSubTagWithTextBody(repositoryTag, "url", repository.url)
            if (repository.snapshotEnabled != null) {
                val snapshotsTag = createSubTag(repositoryTag, "snapshots")
                addSubTagWithTextBody(snapshotsTag, "enabled", repository.snapshotEnabled!!.toString())
            }
        }
    }

    private fun getTagText(parent: XmlTag, name: String): String {
        val subTag = parent.findFirstSubTag(name)
        return subTag?.value?.text!!
    }

    private fun addBom(bom: DepResponse.Bom) {
        val dependencyManagementTag = getOrCreateXmlTag(this.rootTag!!, "dependencyManagement")
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

    private fun getOrCreateXmlTag(parent: XmlTag, name: String): XmlTag {
        var subTag = parent.findFirstSubTag(name)
        if (subTag == null) {
            createSubTag(parent, name)
            subTag = parent.findFirstSubTag(name)
        }
        return subTag!!
    }

    private fun addSubTagWithTextBody(parent: XmlTag?, key: String, value: String?) {
        if (parent != null && StringUtils.isNotEmpty(value) && StringUtils.isNotEmpty(key)) {
            parent.addSubTag(parent.createChildTag(key, parent.namespace, value, false), false)
        }
    }

    private fun createSubTag(parent: XmlTag, name: String): XmlTag {
        return parent.addSubTag(parent.createChildTag(name, parent.namespace, null, false), false)
    }

    private fun createPoint(vararg info: String): String {
        return StringUtils.join(info, ":")
    }
}
