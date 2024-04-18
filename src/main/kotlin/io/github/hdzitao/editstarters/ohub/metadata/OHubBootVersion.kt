package io.github.hdzitao.editstarters.ohub.metadata

import io.github.hdzitao.editstarters.version.Version

/**
 * oHub配置map
 *
 * @version 3.2.0
 */
class OHubBootVersion(
    var masterEnable: Boolean = true, // 默认打开
    var metaDataList: List<OHubMetaData> = emptyList()
) {

    /**
     * 匹配版本
     */
    fun match(version: Version): OHubMetaData? {
        if (!masterEnable) {
            return null
        }

        for (metaDataElement in metaDataList) {
            if (metaDataElement.enable && metaDataElement.checkVersionRange(version)) {
                return metaDataElement
            }
        }

        return null
    }
}

