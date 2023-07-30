package io.github.hdzitao.editstarters.startspringio;

import io.github.hdzitao.editstarters.startspringio.metadata.MetadataConfig;
import io.github.hdzitao.editstarters.version.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * start.spring.io
 *
 * @version 3.2.0
 */
@Getter
@Setter
@NoArgsConstructor
public class StartSpringIO {

    /**
     * 版本
     */
    private Version version;

    /**
     * 元数据
     */
    private MetadataConfig metadataConfig;

    public StartSpringIO(Version version, MetadataConfig metadataConfig) {
        this.version = version;
        this.metadataConfig = metadataConfig;
    }

    public static String checkMetadataConfigLink(String url) {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        String metadataLink = "/metadata/config";

        if (url.endsWith(metadataLink)) {
            return url;
        }

        return url + metadataLink;
    }
}
