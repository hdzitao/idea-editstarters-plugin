package io.github.hdzitao.editstarters.ohub.metadata;

import io.github.hdzitao.editstarters.version.Version;
import io.github.hdzitao.editstarters.version.Versions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * oHub配置元素
 *
 * @version 3.2.0
 */
@Getter
@Setter
@NoArgsConstructor
public class OHubMetaData {
    private String versionRange;
    private String metadataConfig;
    private boolean enable = false; // 默认关闭

    /**
     * 检查版本
     *
     * @param version
     * @return
     */
    public boolean checkVersionRange(Version version) {
        if (StringUtils.isEmpty(versionRange)) {
            return false;
        }

        return Versions.parseRange(versionRange).match(version);
    }
}
