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
                    bom = bom.resolve(version);
                    starterInfo.setBom(bom);
                    for (String rid : bom.getRepositories()) {
                        Repository repository = this.configuration.env.repositories.get(rid);
                        repository = repository.resolve(rid);
                        starterInfo.addRepository(repository);
                    }
                }

                Repository repository = this.configuration.env.repositories.get(repositoryId);
                if (repository != null) {
                    starterInfo.addRepository(repository.resolve(repositoryId));
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
    public static class Bom implements IBom {
        private String groupId;
        private String artifactId;
        private String version;
        private List<String> repositories = new ArrayList<>();
        private List<Bom> mappings;
        // mapping 字段
        private String compatibilityRange;

        public Bom resolve(Version version) {
            Bom bom = new Bom();
            bom.groupId = this.groupId;
            bom.artifactId = this.artifactId;
            bom.version = this.version;
            bom.repositories = this.repositories;

            List<Bom> bomMappings = this.getMappings();
            if (CollectionUtils.isNotEmpty(bomMappings)) {
                for (Bom mapping : bomMappings) {
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
    public static class Repository implements IRepository {
        private String id;
        private String name;
        private String url;
        private boolean releasesEnabled = true;
        private boolean snapshotsEnabled;

        @Override
        public boolean isSnapshotEnabled() { // snapshot(s)Enabled/snapshotEnabled这两个字段是一个意思
            return this.snapshotsEnabled;
        }

        public Repository resolve(String rid) {
            Repository repository = new Repository();
            repository.id = rid;
            repository.name = this.name;
            repository.url = this.url;
            repository.snapshotsEnabled = this.snapshotsEnabled;
            return repository;
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
