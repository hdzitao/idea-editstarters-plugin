package io.github.hdzitao.editstarters.startspringio.metadata;

import com.intellij.util.containers.ContainerUtil;
import io.github.hdzitao.editstarters.dependency.Bom;
import io.github.hdzitao.editstarters.version.Version;
import io.github.hdzitao.editstarters.version.Versions;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * configuration.env.boms
 *
 * @version 3.2.0
 */
public class MetaDataBom extends Bom {
    private List<String> repositories;
    private List<MetaDataBom> mappings;
    // mapping 字段
    private String compatibilityRange;

    public List<String> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<String> repositories) {
        this.repositories = repositories;
    }

    public List<MetaDataBom> getMappings() {
        return mappings;
    }

    public void setMappings(List<MetaDataBom> mappings) {
        this.mappings = mappings;
    }

    public String getCompatibilityRange() {
        return compatibilityRange;
    }

    public void setCompatibilityRange(String compatibilityRange) {
        this.compatibilityRange = compatibilityRange;
    }

    /**
     * 根据版本处理
     */
    public MetaDataBom resolve(Version version) {
        MetaDataBom bom = new MetaDataBom();
        bom.groupId = this.groupId;
        bom.artifactId = this.artifactId;
        bom.version = this.version;
        bom.repositories = this.repositories;

        if (ContainerUtil.isEmpty(this.mappings)) {
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
        if (!ContainerUtil.isEmpty(mapping.repositories)) {
            bom.repositories = mapping.repositories;
        }

        return bom;
    }
}
