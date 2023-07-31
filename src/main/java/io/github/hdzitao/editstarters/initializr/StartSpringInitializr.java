package io.github.hdzitao.editstarters.initializr;

import com.google.gson.Gson;
import com.intellij.util.io.HttpRequests;
import io.github.hdzitao.editstarters.springboot.SpringBootBuilder;
import io.github.hdzitao.editstarters.startspringio.StartSpringIO;
import io.github.hdzitao.editstarters.startspringio.StartSpringIO2SpringBoot;
import io.github.hdzitao.editstarters.startspringio.metadata.MetadataConfig;
import io.github.hdzitao.editstarters.version.Version;
import lombok.SneakyThrows;

/**
 * 请求start.spring.io初始化
 *
 * @version 3.2.0
 */
public class StartSpringInitializr implements Initializr {
    private final Gson gson = new Gson();
    private final SpringBootBuilder<StartSpringIO> builder = new StartSpringIO2SpringBoot();

    @Override
    @SneakyThrows
    public void initialize(InitializrParameter parameter, InitializrReturn ret, InitializrChain chain) {
        Version version = parameter.getVersion();
        String url = StartSpringIO.checkMetadataConfigLink(parameter.getUrl());

        MetadataConfig metadata = HttpRequests.request(url).accept("application/json").connect(request ->
                gson.fromJson(request.readString(), MetadataConfig.class));
        if (metadata == null || !metadata.match(version)) {
            chain.initialize(parameter, ret);
            return;
        }

        StartSpringIO startSpringIO = new StartSpringIO(version, metadata);
        ret.setSpringBoot(builder.buildSpringBoot(startSpringIO));
    }
}
