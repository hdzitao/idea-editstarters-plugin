package com.github.hdzitao.editstarters.initializr;

import com.github.hdzitao.editstarters.dependency.SpringBoot;
import com.google.gson.Gson;
import com.intellij.util.io.HttpRequests;
import lombok.SneakyThrows;

public class SpringInitializr implements Initializr {
    @Override
    @SneakyThrows
    public SpringBoot initialize(InitializrParameters parameters, InitializrStatus status, InitializrChain chain) {
        Gson gson = new Gson();

        StartSpringIO startSpringIO = new StartSpringIO(parameters.getVersion());

        String url = startSpringIO.spliceMetadataConfigLink(parameters.getUrl());
        InitializrMetadataConfig metadata = HttpRequests.request(url).accept("application/json").connect(request ->
                gson.fromJson(request.readString(), InitializrMetadataConfig.class));

        if (!metadata.match(parameters.getVersion())) { // 不匹配配置中的版本,继续传递
            return chain.initialize(parameters, status);
        }

        startSpringIO.setMetadataConfig(metadata);

        return new SpringBoot(parameters.getVersion(), startSpringIO.getDeclaredModules());
    }
}
