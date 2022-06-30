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
    public void initConfigure() {
        String metadataMapUrl = getMetaDataMapUrl();
        JsonArray metadataMap = HttpRequests.request(metadataMapUrl).connect(request ->
                gson.fromJson(request.readString(), JsonArray.class));
        for (JsonElement element : metadataMap) {
            Configure configure = gson.fromJson(element, Configure.class);
            if (configure.enable && Versions.parseRange(configure.versionRange).match(this.version)) {
                // 找到第一个启用的配置
                this.configure = configure;
                return;
            }
        }

        throw new ShowErrorException("Can't find metadata from OthersHub!");
    }

    @SneakyThrows
    public InitializrMetadataConfig getMetaDataConfig() {
        String metadataPath = getMetaDataConfigUrl();
        return HttpRequests.request(metadataPath).connect(request ->
                gson.fromJson(request.readString(), InitializrMetadataConfig.class));
    }

    private String getMetaDataMapUrl() {
        return basePath() + this.site + "/metadata_map.json";
    }

    private String getMetaDataConfigUrl() {
        return basePath() + this.site + "/" + this.configure.metadataConfig + "/metadata_config.json ";
    }

    //==================================================================================================================

    @Getter
    @Setter
    public static class Configure {
        private String versionRange;
        private String metadataConfig;

        private boolean enable = true; // 默认启用
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
