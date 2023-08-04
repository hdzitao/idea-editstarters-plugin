package io.github.hdzitao.editstarters.ohub.metadata;

import io.github.hdzitao.editstarters.version.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * oHub配置map
 *
 * @version 3.2.0
 */
@Getter
@Setter
@NoArgsConstructor
public class OHubBootVersion {
    private boolean masterEnable = true; // 默认打开
    private List<OHubMetaData> metaDataList;

    /**
     * 匹配版本
     */
    public OHubMetaData match(Version version) {
        if (!masterEnable) {
            return null;
        }

        for (OHubMetaData metaDataElement : metaDataList) {
            if (metaDataElement.isEnable() && metaDataElement.checkVersionRange(version)) {
                return metaDataElement;
            }
        }

        return null;
    }
}

