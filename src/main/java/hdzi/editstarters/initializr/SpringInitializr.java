package hdzi.editstarters.initializr;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.intellij.util.io.HttpRequests;
import hdzi.editstarters.dependency.SpringBoot;
import lombok.SneakyThrows;

public class SpringInitializr implements Initializr {
    @Override
    @SneakyThrows
    public SpringBoot initialize(InitializrParameters parameters, InitializrChain chain) {
        Gson gson = new Gson();

        StartSpringIOMetadataClient startSpringIO = new StartSpringIOMetadataClient();

        String url = startSpringIO.spliceMetadataLink(parameters.getUrl());
        InitializrMetadataClient metadata = HttpRequests.request(url).accept("application/json").connect(request ->
                gson.fromJson(request.readString(), InitializrMetadataClient.class));
        startSpringIO.setMetaData(parameters.getVersion(), metadata);

        String dependenciesUrl = startSpringIO.getDependenciesUrl();
        InitializrDependencies dependencies;
        try {
            dependencies = HttpRequests.request(dependenciesUrl).connect(request ->
                    gson.fromJson(request.readString(), InitializrDependencies.class));
        } catch (HttpRequests.HttpStatusException e) {
            if (400 == e.getStatusCode()) { // 400 bad request, 官网不支持的版本,尝试othersInitializr
                return chain.initialize(parameters);
            }
            throw e;
        } catch (JsonSyntaxException e) {
            if ("start.aliyun.com".equals(OthersHub.url2site(url))) { // aliyun其他版本会是一个默认的错误页面
                return chain.initialize(parameters);
            }
            throw e;
        }
        startSpringIO.setDependencies(dependencies);

        return new SpringBoot(parameters.getVersion(), startSpringIO.getDeclaredModules());
    }
}
