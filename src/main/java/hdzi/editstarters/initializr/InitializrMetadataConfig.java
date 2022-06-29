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
            modules.add(module);

            module.setName(dependenciesContent.name);
            module.setValues(new ArrayList<>());
            List<CDependencyContent> dependencyContent = dependenciesContent.getContent();
            for (CDependencyContent content : dependencyContent) {
                StarterInfo starterInfo = new StarterInfo();
                module.getValues().add(starterInfo);

                content = content.resolve(version);

                starterInfo.setId(content.id);
                starterInfo.setName(content.name);
                starterInfo.setDescription(content.description);
                starterInfo.setVersionRange(content.compatibilityRange);

                starterInfo.setGroupId(content.getGroupId());
                starterInfo.setArtifactId(content.getArtifactId());
                starterInfo.setVersion(content.getVersion());
                starterInfo.setScope(content.getScope());

                String bomId = content.bom;
                CBom bom;
                if (StringUtils.isNoneBlank(bomId) && (bom = this.configuration.env.boms.get(bomId)) != null) {
                    bom = bom.resolve(version);

                    starterInfo.setBom(bom);

                    List<String> repositories = bom.getRepositories();
                    if (CollectionUtils.isNotEmpty(repositories)) {
                        for (String rid : repositories) {
                            CRepository repository = this.configuration.env.repositories.get(rid);

                            repository = repository.resolve();

                            starterInfo.addRepository(rid, repository);
                        }
                    }
                }

                String repositoryId = content.repository;
                if (StringUtils.isNoneBlank(repositoryId)) {
                    CRepository repository = this.configuration.env.repositories.get(repositoryId);

                    repository = repository.resolve();

                    starterInfo.addRepository(repositoryId, repository);
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
        private List<String> repositories;
        private List<CBom> mappings;
        // mapping 字段
        private String compatibilityRange;

        public CBom resolve(Version version) {
            CBom bom = new CBom();
            bom.groupId = this.groupId;
            bom.artifactId = this.artifactId;
            bom.version = this.version;
            bom.repositories = this.repositories;

            if (CollectionUtils.isEmpty(this.mappings)) {
                return bom;
            }

            for (CBom mapping : this.mappings) {
                if (Versions.parseRange(mapping.compatibilityRange).match(version)) {
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
    public static class CDependencyContent extends Dependency {
        private String id;
        private String name;
        private String description;
        private List<Link> links;
        private String compatibilityRange;
        private String bom;
        private String repository;
        private List<CDependencyContent> mappings;

        public CDependencyContent resolve(Version version) {
            CDependencyContent dependency = new CDependencyContent();

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

            for (CDependencyContent mapping : this.mappings) {
                if (Versions.parseRange(mapping.compatibilityRange).match(version)) {
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

            return dependency;
        }
    }
}
