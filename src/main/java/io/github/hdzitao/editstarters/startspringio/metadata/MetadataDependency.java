package io.github.hdzitao.editstarters.startspringio.metadata;

import com.intellij.util.containers.ContainerUtil;
import io.github.hdzitao.editstarters.dependency.Dependency;
import io.github.hdzitao.editstarters.version.Version;
import io.github.hdzitao.editstarters.version.Versions;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * dependencies.content.content
 *
 * @version 3.2.0
 */
public class MetadataDependency extends Dependency {
    private String id;
    private String name;
    private String description;
    private List<Link> links;
    private String compatibilityRange;
    private String bom;
    private String repository;
    private List<MetadataDependency> mappings;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public String getCompatibilityRange() {
        return compatibilityRange;
    }

    public void setCompatibilityRange(String compatibilityRange) {
        this.compatibilityRange = compatibilityRange;
    }

    public String getBom() {
        return bom;
    }

    public void setBom(String bom) {
        this.bom = bom;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public List<MetadataDependency> getMappings() {
        return mappings;
    }

    public void setMappings(List<MetadataDependency> mappings) {
        this.mappings = mappings;
    }

    /**
     * 根据版本处理
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

        if (ContainerUtil.isEmpty(this.mappings)) {
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
