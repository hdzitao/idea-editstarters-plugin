package hdzi.editstarters.initializr.chain;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.intellij.util.io.HttpRequests;
import hdzi.editstarters.buildsystem.BuildSystem;
import hdzi.editstarters.dependency.SpringBoot;
import hdzi.editstarters.initializr.StartSpringIO;
import hdzi.editstarters.ui.ShowErrorException;

import java.io.IOException;

public class SpringInitializr implements Initializr {
    @Override
    public SpringBoot initialize(InitializrParameters parameters, InitializrChain chain) {
        try {
            BuildSystem buildSystem = parameters.getBuildSystem();
            String version = buildSystem.getSpringbootDependency().getVersion();
            String currentVersionID = version.replaceFirst("^(\\d+\\.\\d+\\.\\d+).*$", "$1");

            Gson gson = new Gson();
            StartSpringIO startSpringIO = new StartSpringIO();

            String url = startSpringIO.spliceMetadataLink(parameters.getUrl());
            JsonObject metadataJson = HttpRequests.request(url).accept("application/json").connect(request ->
                    gson.fromJson(request.readString(), JsonObject.class));
            startSpringIO.setMetaData(currentVersionID, metadataJson);

            String dependenciesUrl = startSpringIO.getMetaData().getDependenciesUrl();
            JsonObject depsJSON = HttpRequests.request(dependenciesUrl).connect(request ->
                    gson.fromJson(request.readString(), JsonObject.class));
            startSpringIO.setDependencies(depsJSON);

            return new SpringBoot(currentVersionID, startSpringIO.getModules());
        } catch (IOException e) {
            throw new ShowErrorException("Request failure! Your spring boot version may not be supported, please confirm.", e);
        } catch (JsonSyntaxException e) {
            throw new ShowErrorException("Request failure! JSON syntax error for response, please confirm.", e);
        }
    }
}
