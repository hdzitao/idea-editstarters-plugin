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
    public void initialize(InitializrRequest request, InitializrResponse response, InitializrChain chain) {
        Version version = request.getVersion();
        String url = StartSpringIO.checkMetadataConfigLink(request.getUrl());

        MetadataConfig metadata = HttpRequests.request(url).accept("application/json").connect(req ->
                gson.fromJson(req.readString(), MetadataConfig.class));
        if (metadata == null || !metadata.match(version)) {
            chain.initialize(request, response);
            return;
        }

        StartSpringIO startSpringIO = new StartSpringIO(version, metadata);
        response.setSpringBoot(builder.buildSpringBoot(startSpringIO));
    }

    fun checkMetadataConfigLink(url:String):

    String {
        var myUrl = url
        if (myUrl.endsWith("/")) {
            myUrl = myUrl.substring(0, myUrl.length - 1)
        }

        val metadataLink = "/metadata/config"

        if (myUrl.endsWith(metadataLink)) {
            return myUrl
        }

        return myUrl + metadataLink
    }

}
