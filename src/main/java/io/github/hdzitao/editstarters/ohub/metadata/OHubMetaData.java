package io.github.hdzitao.editstarters.ohub.metadata;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private boolean enable = true; // 默认启用
}
