package io.github.hdzitao.editstarters.ohub;

/**
 * spring boot旧版本处理
 *
 * @version 3.2.0
 */
public abstract class OHub {
    /**
     * 基础url
     */
    protected abstract String basePath();

    /**
     * 名字
     */
    public abstract String getName();

    /**
     * metadata_map路径
     */
    public String getMetadataMapUrl() {
        return basePath() + "/metadata_map.json";
    }

    /**
     * 获取metadata路径
     */
    public String getMetadataUrl(String suffix) {
        return basePath() + suffix;
    }

    public final String toString() {
        return getName();
    }
}
