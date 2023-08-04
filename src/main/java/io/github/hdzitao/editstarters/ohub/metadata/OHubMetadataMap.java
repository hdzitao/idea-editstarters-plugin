package io.github.hdzitao.editstarters.ohub.metadata;

import io.github.hdzitao.editstarters.ui.ShowErrorException;
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
public class OHubMetadataMap {
    private List<OHubMetaData> metaDataList;

    /**
     * 匹配版本
     */
    public OHubMetaData match(Version version) {
        for (OHubMetaData metaDataElement : metaDataList) {
            if (metaDataElement.isEnable() && metaDataElement.checkVersionRange(version)) {
                return metaDataElement;
            }
        }

        throw new ShowErrorException("Can't find metadata from OthersHub!");
    }
}

