package io.github.hdzitao.editstarters.startspringio.metadata;

import io.github.hdzitao.editstarters.version.Version;
import io.github.hdzitao.editstarters.version.Versions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

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

    /**
     * 判断版本是否匹配
     *
     * @param version
     * @return
     */
    public boolean match(Version version) {
        String compatibilityRange = Optional.ofNullable(configuration)
                .map(Configuration::getEnv)
                .map(Env::getPlatform)
                .map(Platform::getCompatibilityRange)
                .orElse(null);

        if (StringUtils.isEmpty(compatibilityRange)) {
            return false;
        }

        return Versions.parseRange(compatibilityRange).match(version);
    }
}
