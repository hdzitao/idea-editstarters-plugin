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

        val mapping = this.mappings?.first {
            it.compatibilityRange != null && Versions.parseRange(it.compatibilityRange!!).match(version)
        }

        if (!mapping?.groupId.isNullOrBlank()) {
            bom.groupId = mapping!!.groupId
        }

        if (!mapping?.artifactId.isNullOrBlank()) {
            bom.artifactId = mapping!!.artifactId
        }

        if (!mapping?.version.isNullOrBlank()) {
            bom.version = mapping!!.version
        }

        if (!mapping?.repositories.isNullOrEmpty()) {
            bom.repositories = mapping!!.repositories
        }

        return bom
    }
}