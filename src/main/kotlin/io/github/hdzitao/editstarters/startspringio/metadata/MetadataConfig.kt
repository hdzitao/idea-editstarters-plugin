package io.github.hdzitao.editstarters.startspringio.metadata

import io.github.hdzitao.editstarters.version.Version
import io.github.hdzitao.editstarters.version.Versions

/**
 * /metadata/config 接口
 *
 * @version 3.2.0
 */
class MetadataConfig(
    var configuration: Configuration? = null,
    var dependencies: Dependencies? = null
) {
    /**
     * 判断版本是否匹配
     */
    fun match(version: Version): Boolean = this.configuration?.env?.platform?.compatibilityRange?.let {
        return Versions.parseRange(it).match(version)
    } ?: false
}
