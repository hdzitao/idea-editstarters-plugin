package io.github.hdzitao.editstarters.ohub.metadata;

import io.github.hdzitao.editstarters.version.Version;

import java.util.List;

/**
 * oHub配置map
 *
 * @version 3.2.0
 */
public class OHubBootVersion {
    private boolean masterEnable = true; // 默认打开
    private List<OHubMetaData> metaDataList;

    public boolean isMasterEnable() {
        return masterEnable;
    }

    public void setMasterEnable(boolean masterEnable) {
        this.masterEnable = masterEnable;
    }

    public List<OHubMetaData> getMetaDataList() {
        return metaDataList;
    }

    public void setMetaDataList(List<OHubMetaData> metaDataList) {
        this.metaDataList = metaDataList;
    }

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

