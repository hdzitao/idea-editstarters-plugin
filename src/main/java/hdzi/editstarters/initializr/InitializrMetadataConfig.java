package hdzi.editstarters.initializr;

import hdzi.editstarters.dependency.Module;
import hdzi.editstarters.dependency.*;
import hdzi.editstarters.version.Version;
import hdzi.editstarters.version.Versions;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class InitializrMetadataConfig {
    private CConfiguration configuration;
    private CDependencies dependencies;

    public List<Module> getModules(Version version) {
        List<Module> modules = new ArrayList<>();
        for (CDependenciesContent dependenciesContent : dependencies.content) {
            Module module = new Module();
            module.setName(dependenciesContent.name);
            module.setValues(new ArrayList<>());
            List<CDependencyContent> dependencyContent = dependenciesContent.getContent();
            for (CDependencyContent content : dependencyContent) {
                StarterInfo starterInfo = new StarterInfo();
                starterInfo.setId(content.id);
                starterInfo.setName(content.name);
                starterInfo.setDescription(content.description);
                starterInfo.setVersionRange(content.compatibilityRange);

                starterInfo.setGroupId(content.groupId);
                starterInfo.setArtifactId(content.artifactId);
                starterInfo.setVersion(content.version);

                String repositoryId = content.repository;
                String bomId = content.bom;

                List<CDependencyContent> dependencyMappings = content.getMappings();
                if (CollectionUtils.isNotEmpty(dependencyMappings)) {
                    for (CDependencyContent mapping : dependencyMappings) {
                        if (Versions.parseRange(mapping.compatibilityRange).match(version)) {
                            if (StringUtils.isNoneBlank(mapping.groupId)) {
                                starterInfo.setGroupId(mapping.groupId);
                            }
                            if (StringUtils.isNoneBlank(mapping.artifactId)) {
                                starterInfo.setArtifactId(mapping.artifactId);
                            }
                            if (StringUtils.isNoneBlank(mapping.version)) {
                                starterInfo.setVersion(mapping.version);
                            }
                            if (StringUtils.isNoneBlank(mapping.repository)) {
                                repositoryId = mapping.repository;
                            }
                            if (StringUtils.isNoneBlank(mapping.bom)) {
                                bomId = mapping.bom;
                            }
                            break;
                        }
                    }
                }

                CBom bom = this.configuration.env.boms.get(bomId);
                if (bom != null) {
                    bom = bom.resolve(version);
                    starterInfo.setBom(bom);
                    for (String rid : bom.getRepositories()) {
                        CRepository repository = this.configuration.env.repositories.get(rid);
                        repository = repository.resolve();
                        starterInfo.addRepository(rid, repository);
                    }
                }

                CRepository repository = this.configuration.env.repositories.get(repositoryId);
                if (repository != null) {
                    starterInfo.addRepository(repositoryId, repository.resolve());
                }
            }
        }

        return modules;
    }

    //==================================================================================================================

    @Getter
    @Setter
    public static class CConfiguration {
        private CEnv env;
    }

    @Getter
    @Setter
    public static class CEnv {
        private Map<String, CBom> boms;
        private Map<String, CRepository> repositories;
    }

    @Getter
    @Setter
    public static class CBom extends Bom {
        private List<String> repositories = new ArrayList<>();
        private List<CBom> mappings;
        // mapping 字段
        private String compatibilityRange;

        public CBom resolve(Version version) {
            CBom bom = new CBom();
            bom.groupId = this.groupId;
            bom.artifactId = this.artifactId;
            bom.version = this.version;
            bom.repositories = this.repositories;

            List<CBom> bomMappings = this.getMappings();
            if (CollectionUtils.isNotEmpty(bomMappings)) {
                for (CBom mapping : bomMappings) {
                    if (Versions.parseRange(mapping.compatibilityRange).match(version)) {
                        if (StringUtils.isNoneBlank(this.groupId)) {
                            bom.groupId = this.groupId;
                        }
                        if (StringUtils.isNoneBlank(this.artifactId)) {
                            bom.artifactId = this.artifactId;
                        }
                        if (StringUtils.isNoneBlank(this.version)) {
                            bom.version = this.version;
                        }
                        if (CollectionUtils.isNotEmpty(this.repositories)) {
                            bom.repositories = this.repositories;
                        }
                    }
                }
            }

            return bom;
        }
    }

    @Getter
    @Setter
    public static class CRepository extends Repository {
        private boolean releasesEnabled = true;

        public CRepository resolve() {
            CRepository repository = new CRepository();
            repository.name = this.name;
            repository.url = this.url;
            repository.snapshotEnabled = this.snapshotEnabled;
            return repository;
        }
    }

    @Getter
    @Setter
    public static class CDependencies {
        private List<CDependenciesContent> content;
    }

    @Getter
    @Setter
    public static class CDependenciesContent {
        private String name;
        private List<CDependencyContent> content;
    }

    @Getter
    @Setter
    public static class CDependencyContent {
        private String id;
        private String name;
        private String description;
        private List<Link> links;
        private String groupId;
        private String artifactId;
        private String version;
        private String scope;
        private String compatibilityRange;
        private String bom;
        private String repository;
        private List<CDependencyContent> mappings;
    }
}
