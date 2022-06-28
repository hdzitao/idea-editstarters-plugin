package hdzi.editstarters.initializr;

import hdzi.editstarters.dependency.Link;
import hdzi.editstarters.dependency.Module;
import hdzi.editstarters.dependency.StarterInfo;
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
    private Configuration configuration;
    private Dependencies dependencies;

    public List<Module> getModules(Version version) {
        List<Module> modules = new ArrayList<>();
        for (DependenciesContent dependenciesContent : dependencies.content) {
            Module module = new Module();
            module.setName(dependenciesContent.name);
            module.setValues(new ArrayList<>());
            List<DependencyContent> dependencyContent = dependenciesContent.getContent();
            for (DependencyContent content : dependencyContent) {
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

                List<DependencyContent> dependencyMappings = content.getMappings();
                if (CollectionUtils.isNotEmpty(dependencyMappings)) {
                    for (DependencyContent mapping : dependencyMappings) {
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

                Bom bom = this.configuration.env.boms.get(bomId);
                if (bom != null) {
                    InitializrBom initializrBom = bom.initializr(version);
                    for (String rid : initializrBom.getRepositories()) {
                        Repository repository = this.configuration.env.repositories.get(rid);
                        InitializrRepository initializrRepository = repository.initializr(rid);
                        starterInfo.addRepository(initializrRepository);
                    }
                }

                Repository repository = this.configuration.env.repositories.get(repositoryId);
                if (repository != null) {
                    starterInfo.addRepository(repository.initializr(repositoryId));
                }
            }
        }

        return modules;
    }

    //==================================================================================================================

    @Getter
    @Setter
    public static class Configuration {
        private Env env;
    }

    @Getter
    @Setter
    public static class Env {
        private Map<String, Bom> boms;
        private Map<String, Repository> repositories;
    }

    @Getter
    @Setter
    public static class Bom {
        private String groupId;
        private String artifactId;
        private String version;
        private List<String> repositories = new ArrayList<>();
        private List<Bom> mappings;
        // mapping 字段
        private String compatibilityRange;

        public InitializrBom initializr(Version version) {
            InitializrBom initializrBom = new InitializrBom();
            initializrBom.setGroupId(this.groupId);
            initializrBom.setArtifactId(this.artifactId);
            initializrBom.setVersion(this.version);
            initializrBom.setRepositories(this.repositories);

            List<Bom> bomMappings = this.getMappings();
            if (CollectionUtils.isNotEmpty(bomMappings)) {
                for (Bom mapping : bomMappings) {
                    if (Versions.parseRange(mapping.compatibilityRange).match(version)) {
                        if (StringUtils.isNoneBlank(this.groupId)) {
                            initializrBom.setGroupId(this.groupId);
                        }
                        if (StringUtils.isNoneBlank(this.artifactId)) {
                            initializrBom.setArtifactId(this.artifactId);
                        }
                        if (StringUtils.isNoneBlank(this.version)) {
                            initializrBom.setVersion(this.version);
                        }
                        if (CollectionUtils.isNotEmpty(this.repositories)) {
                            initializrBom.setRepositories(this.repositories);
                        }
                    }
                }
            }

            return initializrBom;
        }
    }

    @Getter
    @Setter
    public static class Repository {
        private String name;
        private String url;
        private boolean releasesEnabled = true;
        private boolean snapshotsEnabled;

        public InitializrRepository initializr(String rid) {
            InitializrRepository initializrRepository = new InitializrRepository();
            initializrRepository.setId(rid);
            initializrRepository.setName(this.name);
            initializrRepository.setUrl(this.url);
            initializrRepository.setSnapshotEnabled(this.snapshotsEnabled);
            return initializrRepository;
        }
    }

    @Getter
    @Setter
    public static class Dependencies {
        private List<DependenciesContent> content;
    }

    @Getter
    @Setter
    public static class DependenciesContent {
        private String name;
        private List<DependencyContent> content;
    }

    @Getter
    @Setter
    public static class DependencyContent {
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
        private List<DependencyContent> mappings;
    }
}
