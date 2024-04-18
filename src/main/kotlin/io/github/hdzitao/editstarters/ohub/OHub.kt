package io.github.hdzitao.editstarters.ohub

/**
 * spring boot旧版本处理
 *
 * @version 3.2.0
 */
abstract class OHub(
    val name: String,
    val basePath: String
) {

    /**
     * metadata_map路径
     */
    val metadataMapUrl: String
        get() = "$basePath/metadata_map.json"

    /**
     * 获取metadata路径
     */
    fun getMetadataUrl(suffix: String): String {
        return basePath + suffix
    }

    override fun toString(): String {
        return name
    }
}
