package io.github.hdzitao.editstarters.ohub;

import io.github.hdzitao.editstarters.version.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * spring boot旧版本处理
 *
 * @version 3.2.0
 */
@Getter
@Setter
@AllArgsConstructor
public abstract class OHub {
    private final String site;
    private final Version version;

    /**
     * 基础url
     *
     * @return
     */
    protected abstract String basePath();

    /**
     * 名字
     *
     * @return
     */
    public abstract String getName();

    /**
     * metadata_map路径
     *
     * @return
     */
    public String getMetadataMapUrl() {
        return basePath() + site + "/metadata_map.json";
    }

    /**
     * 获取metadata路径
     *
     * @param suffix
     * @return
     */
    public String getMetadataUrl(String suffix) {
        return basePath() + site + suffix;
    }

    /**
     * url => site
     *
     * @param url
     * @return
     */
    public static String url2site(String url) {
        return url.replaceFirst("^.*?//([^/]+).*$", "$1");
    }

    public final String toString() {
        return getName();
    }
}
