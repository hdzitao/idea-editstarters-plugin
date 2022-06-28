package hdzi.editstarters.initializr;

import hdzi.editstarters.dependency.Link;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class InitializrMetadataConfig {
    private Configuration configuration;
    private Dependencies dependencies;

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
    }

    @Getter
    @Setter
    public static class Repository {
        private String name;
        private URL url;
        private boolean releasesEnabled = true;
        private boolean snapshotsEnabled;
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
