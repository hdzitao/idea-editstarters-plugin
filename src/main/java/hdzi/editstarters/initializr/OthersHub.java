package hdzi.editstarters.initializr;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.intellij.util.io.HttpRequests;
import hdzi.editstarters.ui.ShowErrorException;
import hdzi.editstarters.version.Version;
import hdzi.editstarters.version.Versions;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

public abstract class OthersHub {
    @Getter
    private final String site;

    @Getter
    private final Version version;

    @Getter
    private Configure configure;

    private final Gson gson = new Gson();

    protected OthersHub(String site, Version version) {
        this.site = site;
        this.version = version;
    }

    public static String url2site(String url) {
        return url.replaceFirst("^.*?//([^/]+).*$", "$1");
    }

    protected abstract String basePath();

    public abstract String toString();

    @SneakyThrows
    public InitializrMetadataClient getMetaData() {
        String metadataMapUrl = getMetaDataMapUrl();
        JsonArray metadataMap = HttpRequests.request(metadataMapUrl).connect(request ->
                gson.fromJson(request.readString(), JsonArray.class));
        for (JsonElement element : metadataMap) {
            Configure configure = gson.fromJson(element, Configure.class);
            if (Versions.parseRange(configure.versionRange).match(this.version)) {
                this.configure = configure;
                String metadataPath = getMetaDataUrl();
                return HttpRequests.request(metadataPath).connect(request ->
                        gson.fromJson(request.readString(), InitializrMetadataClient.class));
            }
        }

        throw new ShowErrorException("Can't find metadata from OthersHub!");
    }

    @SneakyThrows
    public InitializrDependencies getDependencies() {
        String dependenciesUrl = getDependenciesUrl();
        return HttpRequests.request(dependenciesUrl).connect(request ->
                gson.fromJson(request.readString(), InitializrDependencies.class));
    }

    private String getMetaDataMapUrl() {
        return basePath() + this.site + "/metadata_map.json";
    }

    private String getMetaDataUrl() {
        return basePath() + this.site + "/" + this.configure.metadata + "/metadata.json ";
    }

    private String getDependenciesUrl() {
        return basePath() + this.site + "/" + configure.dependencies + "/dependencies.json";
    }

    //==================================================================================================================

    @Getter
    @Setter
    public static class Configure {
        private String versionRange;
        private String metadata;
        private String dependencies;
    }

    public static class GitHub extends OthersHub {
        public GitHub(String site, Version version) {
            super(site, version);
        }

        @Override
        protected String basePath() {
            return "https://raw.githubusercontent.com/hdzitao/idea-editstarters-plugin-configure/main/bootVersion/";
        }

        @Override
        public String toString() {
            return "GitHub";
        }
    }

//    public static class Gitee extends OthersHub {
//        public Gitee(String site, Version version) {
//            super(site, version);
//        }
//
//        @Override
//        protected String basePath() {
//            return "https://gitee.com/hdzitao/idea-editstarters-plugin-configure/raw/master/bootVersion/";
//        }
//
//    }
}
