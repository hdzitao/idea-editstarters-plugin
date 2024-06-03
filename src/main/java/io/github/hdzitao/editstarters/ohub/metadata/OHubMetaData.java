package io.github.hdzitao.editstarters.ohub.metadata;

import io.github.hdzitao.editstarters.version.Version;
import io.github.hdzitao.editstarters.version.Versions;
import org.apache.commons.lang3.StringUtils;

/**
 * oHub配置元素
 *
 * @version 3.2.0
 */
public class OHubMetaData {
    private String versionRange;
    private String metadataConfig;
    private boolean enable = false; // 默认关闭

    public String getVersionRange() {
        return versionRange;
    }

    public void setVersionRange(String versionRange) {
        this.versionRange = versionRange;
    }

    public String getMetadataConfig() {
        return metadataConfig;
    }

    public void setMetadataConfig(String metadataConfig) {
        this.metadataConfig = metadataConfig;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    /**
     * 检查版本
     */
    public boolean checkVersionRange(Version version) {
        if (StringUtils.isEmpty(versionRange)) {
            return false;
        }

        return Versions.parseRange(versionRange).match(version);
    }
}
