package io.github.hdzitao.editstarters.initializr;

import com.google.gson.Gson;
import com.intellij.util.io.HttpRequests;
import io.github.hdzitao.editstarters.cache.InitializrCache;
import io.github.hdzitao.editstarters.ohub.OHub;
import io.github.hdzitao.editstarters.ohub.metadata.OHubBootVersion;
import io.github.hdzitao.editstarters.ohub.metadata.OHubMetaData;
import io.github.hdzitao.editstarters.springboot.SpringBootBuilder;
import io.github.hdzitao.editstarters.startspringio.StartSpringIO;
import io.github.hdzitao.editstarters.startspringio.StartSpringIO2SpringBoot;
import io.github.hdzitao.editstarters.startspringio.metadata.MetadataConfig;
import io.github.hdzitao.editstarters.version.Version;

/**
 * 旧版本初始化
 *
 * @version 3.2.0
 */
public class OHubInitializr implements Initializr {
    private final Gson gson = new Gson();
    private final SpringBootBuilder<StartSpringIO> springIO2SpringBoot = new StartSpringIO2SpringBoot();

    @Override
    public void initialize(InitializrRequest request, InitializrResponse response, InitializrChain chain) throws Exception {
        Version version = request.getVersion();
        OHub oHub = request.getSelectedOHub();

        if (oHub == null) {
            chain.initialize(request, response);
            return;
        }

        response.setEnableOHub(true);
        response.setOHub(oHub);

        // 初始化旧版本配置
        String metadataMapUrl = oHub.getMetadataMapUrl();
        OHubBootVersion oHubBootVersion = HttpRequests.request(metadataMapUrl).connect(req ->
                gson.fromJson(req.readString(), OHubBootVersion.class));
        OHubMetaData oHubMetaData = oHubBootVersion.match(version);
        if (oHubMetaData == null) {
            chain.initialize(request, response);
            return;
        }
        // 获取旧版本metadata
        String metadataUrl = oHub.getMetadataUrl(oHubMetaData.getMetadataConfig());
        MetadataConfig metadata = HttpRequests.request(metadataUrl).connect(req ->
                gson.fromJson(req.readString(), MetadataConfig.class));
        // 使用startspringio解析
        StartSpringIO startSpringIO = new StartSpringIO(version, metadata);
        response.setSpringBoot(springIO2SpringBoot.buildSpringBoot(startSpringIO));

        // 缓存
        InitializrCache initializrCache = InitializrCache.getInstance(request.getProject());
        initializrCache.putOHubName(oHub.getName());
    }
}
