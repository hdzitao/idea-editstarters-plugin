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

    protected abstract String basePath();

    public abstract String getName();

    public String getMetadataMapUrl() {
        return basePath() + site + "/metadata_map.json";
    }

    public String getMetadataUrl(String suffix) {
        return basePath() + site + suffix;
    }

    public static String url2site(String url) {
        return url.replaceFirst("^.*?//([^/]+).*$", "$1");
    }

    public final String toString() {
        return getName();
    }
}
