package io.github.hdzitao.editstarters.startspringio.metadata

import io.github.hdzitao.editstarters.dependency.Bom
import io.github.hdzitao.editstarters.version.Version
import io.github.hdzitao.editstarters.version.Versions

/**
 * configuration.env.boms
 *
 * @version 3.2.0
 */
class MetaDataBom(
    var repositories: List<String>? = null,
    var mappings: List<MetaDataBom>? = null,
    var compatibilityRange: String? = null
) : Bom() {

    /**
     * 根据版本处理
     */
    fun resolve(version: Version): MetaDataBom {
        val bom = MetaDataBom()
        bom.groupId = this.groupId
        bom.artifactId = this.artifactId
        bom.version = this.version
        bom.repositories = this.repositories

        val mapping = this.mappings?.first { Versions.parseRange(it.compatibilityRange).match(version) }

        if (mapping?.groupId?.isNotBlank() == true) {
            bom.groupId = mapping.groupId
        }

        if (mapping?.artifactId?.isNotBlank() == true) {
            bom.artifactId = mapping.artifactId
        }

        if (mapping?.version?.isNotBlank() == true) {
            bom.version = mapping.version
        }

        if (mapping?.repositories?.isNotEmpty() == true) {
            bom.repositories = mapping.repositories
        }

        return bom
    }
}