package io.github.hdzitao.editstarters.startspringio.metadata;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * /metadata/config 接口
 *
 * @version 3.2.0
 */
@Getter
@Setter
@NoArgsConstructor
public class MetadataConfig {
    private Configuration configuration;
    private Dependencies dependencies;
}
