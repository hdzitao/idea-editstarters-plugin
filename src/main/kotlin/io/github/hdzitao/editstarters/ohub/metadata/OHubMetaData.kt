package io.github.hdzitao.editstarters.ohub.metadata

import io.github.hdzitao.editstarters.version.Version
import io.github.hdzitao.editstarters.version.Versions

/**
 * oHub配置元素
 *
 * @version 3.2.0
 */
class OHubMetaData(
    var versionRange: String? = null,
    var metadataConfig: String? = null,
    var enable: Boolean = false // 默认关闭
) {

    /**
     * 检查版本
     */
    fun checkVersionRange(version: Version): Boolean {
        return if (versionRange.isNullOrBlank()) {
            false
        } else {
            Versions.parseRange(versionRange!!).match(version)
        }
    }
}
