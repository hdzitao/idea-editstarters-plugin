package io.github.hdzitao.editstarters.initializr;

import com.google.gson.Gson;
import com.intellij.util.io.HttpRequests;
import io.github.hdzitao.editstarters.ohub.OHub;
import io.github.hdzitao.editstarters.ohub.metadata.OHubMetaData;
import io.github.hdzitao.editstarters.ohub.metadata.OHubMetadataMap;
import io.github.hdzitao.editstarters.springboot.SpringBootBuilder;
import io.github.hdzitao.editstarters.startspringio.StartSpringIO;
import io.github.hdzitao.editstarters.startspringio.StartSpringIO2SpringBoot;
import io.github.hdzitao.editstarters.startspringio.metadata.MetadataConfig;
import io.github.hdzitao.editstarters.version.Version;
import lombok.SneakyThrows;

/**
 * 旧版本初始化
 *
 * @version 3.2.0
 */
public class OHubInitializr implements Initializr {
    private final Gson gson = new Gson();
    private final SpringBootBuilder<StartSpringIO> springIO2SpringBoot = new StartSpringIO2SpringBoot();

    @Override
    @SneakyThrows
    public void initialize(InitializrParameter parameter, InitializrReturn ret, InitializrChain chain) {
        Version version = parameter.getVersion();
        OHub oHub = parameter.getOHub();

        if (oHub == null) {
            chain.initialize(parameter, ret);
            return;
        }

        ret.setEnableOHub(true);
        ret.setOHub(oHub);

        // 初始化旧版本配置
        String metadataMapUrl = oHub.getMetadataMapUrl();
        OHubMetadataMap oHubMetadataMap = HttpRequests.request(metadataMapUrl).connect(request ->
                gson.fromJson(request.readString(), OHubMetadataMap.class));
        OHubMetaData oHubMetaData = oHubMetadataMap.match(version);
        // 获取旧版本metadata
        String metadataUrl = oHub.getMetadataUrl(oHubMetaData.getMetadataConfig());
        MetadataConfig metadata = HttpRequests.request(metadataUrl).connect(request ->
                gson.fromJson(request.readString(), MetadataConfig.class));
        // 使用startspringio解析
        StartSpringIO startSpringIO = new StartSpringIO(version, metadata);
        ret.setSpringBoot(springIO2SpringBoot.buildSpringBoot(startSpringIO));
    }
}
