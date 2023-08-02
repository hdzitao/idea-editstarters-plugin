package io.github.hdzitao.editstarters.startspringio.metadata;

import io.github.hdzitao.editstarters.dependency.Bom;
import io.github.hdzitao.editstarters.version.Version;
import io.github.hdzitao.editstarters.version.Versions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * configuration.env.boms
 *
 * @version 3.2.0
 */
@Getter
@Setter
@NoArgsConstructor
public class MetaDataBom extends Bom {
    private List<String> repositories;
    private List<MetaDataBom> mappings;
    // mapping 字段
    private String compatibilityRange;

    /**
     * 根据版本处理
     *
     * @param version
     * @return
     */
    public MetaDataBom resolve(Version version) {
        MetaDataBom bom = new MetaDataBom();
        bom.groupId = this.groupId;
        bom.artifactId = this.artifactId;
        bom.version = this.version;
        bom.repositories = this.repositories;

        if (CollectionUtils.isEmpty(this.mappings)) {
            return bom;
        }

        Optional<MetaDataBom> findMapping = this.mappings.stream()
                .filter(mapping -> Versions.parseRange(mapping.compatibilityRange).match(version))
                .findFirst();
        if (!findMapping.isPresent()) {
            return bom;
        }

        MetaDataBom mapping = findMapping.get();
        if (StringUtils.isNoneBlank(mapping.groupId)) {
            bom.groupId = mapping.groupId;
        }
        if (StringUtils.isNoneBlank(mapping.artifactId)) {
            bom.artifactId = mapping.artifactId;
        }
        if (StringUtils.isNoneBlank(mapping.version)) {
            bom.version = mapping.version;
        }
        if (CollectionUtils.isNotEmpty(mapping.repositories)) {
            bom.repositories = mapping.repositories;
        }

        return bom;
    }
}
