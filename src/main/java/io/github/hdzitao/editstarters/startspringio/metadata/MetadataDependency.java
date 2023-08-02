package io.github.hdzitao.editstarters.startspringio.metadata;

import io.github.hdzitao.editstarters.dependency.Dependency;
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
 * dependencies.content.content
 *
 * @version 3.2.0
 */
@Getter
@Setter
@NoArgsConstructor
public class MetadataDependency extends Dependency {
    private String id;
    private String name;
    private String description;
    private List<Link> links;
    private String compatibilityRange;
    private String bom;
    private String repository;
    private List<MetadataDependency> mappings;

    /**
     * 根据版本处理
     *
     * @param version
     * @return
     */
    public MetadataDependency resolve(Version version) {
        MetadataDependency dependency = new MetadataDependency();

        dependency.id = this.id;
        dependency.name = this.name;
        dependency.description = this.description;
        dependency.compatibilityRange = this.compatibilityRange;

        dependency.groupId = this.groupId;
        dependency.artifactId = this.artifactId;
        dependency.version = this.version;
        dependency.scope = this.scope;

        dependency.bom = this.bom;
        dependency.repository = this.repository;

        if (CollectionUtils.isEmpty(this.mappings)) {
            return dependency;
        }

        Optional<MetadataDependency> findMapping = this.mappings.stream()
                .filter(mapping -> Versions.parseRange(mapping.compatibilityRange).match(version))
                .findFirst();
        if (!findMapping.isPresent()) {
            return dependency;
        }

        MetadataDependency mapping = findMapping.get();
        if (StringUtils.isNoneBlank(mapping.groupId)) {
            dependency.groupId = mapping.groupId;
        }
        if (StringUtils.isNoneBlank(mapping.artifactId)) {
            dependency.artifactId = mapping.artifactId;
        }
        if (StringUtils.isNoneBlank(mapping.version)) {
            dependency.version = mapping.version;
        }
        if (StringUtils.isNoneBlank(mapping.bom)) {
            dependency.bom = mapping.bom;
        }
        if (StringUtils.isNoneBlank(mapping.repository)) {
            dependency.repository = mapping.repository;
        }

        return dependency;
    }
}
