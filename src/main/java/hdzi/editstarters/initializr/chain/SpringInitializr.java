package hdzi.editstarters.initializr.chain;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.intellij.util.io.HttpRequests;
import hdzi.editstarters.dependency.SpringBoot;
import hdzi.editstarters.initializr.StartSpringIO;
import lombok.SneakyThrows;

public class SpringInitializr implements Initializr {
    @Override
    @SneakyThrows
    public SpringBoot initialize(InitializrParameters parameters, InitializrChain chain) {
        Gson gson = new Gson();

        String versionID = parameters.getVersion().toVersionID();
        StartSpringIO startSpringIO = new StartSpringIO();

        String url = startSpringIO.spliceMetadataLink(parameters.getUrl());
        JsonObject metadataJson = HttpRequests.request(url).accept("application/json").connect(request ->
                gson.fromJson(request.readString(), JsonObject.class));
        startSpringIO.setMetaData(versionID, metadataJson);

        String dependenciesUrl = startSpringIO.getMetaData().getDependenciesUrl();
        JsonObject depsJSON;
        try {
            depsJSON = HttpRequests.request(dependenciesUrl).connect(request ->
                    gson.fromJson(request.readString(), JsonObject.class));
        } catch (HttpRequests.HttpStatusException e) {
            if (400 == e.getStatusCode()) { // 400 bad request, 官网不支持的版本,尝试othersInitializr
                return chain.initialize(parameters);
            }
            throw e;
        }
        startSpringIO.setDependencies(depsJSON);

        return new SpringBoot(startSpringIO.getMetaData().getVersionID(), startSpringIO.getModules());
    }
}
