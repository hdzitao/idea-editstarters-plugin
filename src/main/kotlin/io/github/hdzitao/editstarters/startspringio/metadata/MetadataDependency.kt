package io.github.hdzitao.editstarters.startspringio.metadata

import io.github.hdzitao.editstarters.dependency.Dependency
import io.github.hdzitao.editstarters.version.Version
import io.github.hdzitao.editstarters.version.Versions

/**
 * dependencies.content.content
 *
 * @version 3.2.0
 */
class MetadataDependency(
    var id: String? = null,
    var name: String? = null,
    var description: String? = null,
    val links: List<Link>? = null,
    var compatibilityRange: String? = null,
    var bom: String? = null,
    var repository: String? = null,
    val mappings: List<MetadataDependency>? = null
) : Dependency() {

    /**
     * 根据版本处理
     */
    fun resolve(version: Version): MetadataDependency {
        val dependency = MetadataDependency()

        dependency.id = this.id
        dependency.name = this.name
        dependency.description = this.description
        dependency.compatibilityRange = this.compatibilityRange

        dependency.groupId = this.groupId
        dependency.artifactId = this.artifactId
        dependency.version = this.version
        dependency.scope = this.scope

        dependency.bom = this.bom
        dependency.repository = this.repository

        val mapping = this.mappings?.first {
            it.compatibilityRange != null && Versions.parseRange(it.compatibilityRange!!).match(version)
        }

        if (!mapping?.groupId.isNullOrBlank()) {
            dependency.groupId = mapping!!.groupId
        }

        if (!mapping?.artifactId.isNullOrBlank()) {
            dependency.artifactId = mapping!!.artifactId
        }

        if (!mapping?.version.isNullOrBlank()) {
            dependency.version = mapping!!.version
        }

        if (!mapping?.bom.isNullOrBlank()) {
            dependency.bom = mapping!!.bom
        }

        if (!mapping?.repository.isNullOrBlank()) {
            dependency.repository = mapping!!.repository
        }

        return dependency
    }
}
