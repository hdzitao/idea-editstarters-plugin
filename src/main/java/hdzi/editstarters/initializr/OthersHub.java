package hdzi.editstarters.initializr;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.util.io.HttpRequests;
import hdzi.editstarters.ui.ShowErrorException;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;

public abstract class OthersHub {
    @Getter
    private final String site;

    @Getter
    private final Versions.Version version;

    @Getter
    private Configure configure;

    protected OthersHub(String site, Versions.Version version) {
        this.site = site;
        this.version = version;
    }

    public static String url2site(String url) {
        return url.replaceFirst("^.*?//([^/]+).*$", "$1");
    }

    protected abstract String basePath();

    public abstract String toString();

    @SneakyThrows
    public JsonObject getMetaData() {
        Gson gson = new Gson();
        String metadataMapUrl = getMetaDataMapUrl();
        JsonArray metadataMap = HttpRequests.request(metadataMapUrl).connect(request ->
                gson.fromJson(request.readString(), JsonArray.class));
        for (JsonElement element : metadataMap) {
            Configure configure = gson.fromJson(element, Configure.class);
            if (this.version.inRange(Versions.parseRange(configure.versionRange))) {
                this.configure = configure;
                String metadataPath = getMetaDataUrl(configure.metadata);
                return HttpRequests.request(metadataPath).connect(request ->
                        gson.fromJson(request.readString(), JsonObject.class));
            }
        }

        throw new ShowErrorException("Can't find metadata from OthersHub!");
    }

    @SneakyThrows
    public JsonObject getDependencies() {
        Gson gson = new Gson();
        String dependenciesUrl = getDependenciesUrl();
        try {
            return HttpRequests.request(dependenciesUrl).connect(request ->
                    gson.fromJson(request.readString(), JsonObject.class));
        } catch (HttpRequests.HttpStatusException e) {
            if (404 == e.getStatusCode()) {
                throw new ShowErrorException("Can't find dependencies from OthersHub!");
            }
            throw e;
        }
    }

    private String getMetaDataMapUrl() {
        return basePath() + this.site + "/metadata_map.json";
    }

    private String getMetaDataUrl(String version) {
        return basePath() + this.site + "/" + version + "/metadata.json ";
    }

    private String getDependenciesUrl() {
        return basePath() + this.site + "/" + configure.dependencies + "/dependencies.json";
    }

    //==================================================================================================================

    @Data
    public static class Configure {
        private String versionRange;
        private String metadata;
        private String dependencies;
    }


    public static class GitHub extends OthersHub {
        public GitHub(String site, Versions.Version version) {
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
//        public Gitee(String site, Versions.Version version) {
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
