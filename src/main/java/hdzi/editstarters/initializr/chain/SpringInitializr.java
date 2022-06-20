package hdzi.editstarters.initializr.chain;

import com.google.gson.*;
import com.intellij.util.io.HttpRequests;
import hdzi.editstarters.buildsystem.BuildSystem;
import hdzi.editstarters.dependency.Dependency;
import hdzi.editstarters.dependency.DependencyScope;
import hdzi.editstarters.dependency.SpringBootProject;
import hdzi.editstarters.dependency.StarterInfo;
import hdzi.editstarters.initializr.InitializrBom;
import hdzi.editstarters.initializr.InitializrDependency;
import hdzi.editstarters.initializr.InitializrResponse;
import hdzi.editstarters.ui.ShowErrorException;

import java.io.IOException;
import java.util.*;

public class SpringInitializr implements Initializr {
    @Override
    public SpringBootProject initialize(InitializrParameters parameters, InitializrChain chain) {
        try {
            BuildSystem buildSystem = parameters.getBuildSystem();
            String version = buildSystem.getSpringbootDependency().getVersion();
            String currentVersionID = version.replaceFirst("^(\\d+\\.\\d+\\.\\d+).*$", "$1");

            Gson gson = new Gson();
            JsonObject baseInfoJSON = HttpRequests.request(parameters.getUrl()).accept("application/json").connect(request ->
                    gson.fromJson(request.readString(), JsonObject.class));
            String dependenciesUrl = parseDependenciesUrl(baseInfoJSON, currentVersionID);
            JsonObject depsJSON = HttpRequests.request(dependenciesUrl).connect(request ->
                    gson.fromJson(request.readString(), JsonObject.class));
            Map<String, List<StarterInfo>> modules = parseDependencies(baseInfoJSON, depsJSON, buildSystem.getExistsDependencyDB());

            return new SpringBootProject(currentVersionID, modules);
        } catch (IOException ignore) {
            throw new ShowErrorException("Request failure! Your spring boot version may not be supported, please confirm.");
        } catch (JsonSyntaxException ignore) {
            throw new ShowErrorException("Request failure! JSON syntax error for response, please confirm.");
        }
    }

    private String parseDependenciesUrl(JsonObject json, String version) {
        return json.getAsJsonObject("_links")
                .getAsJsonObject("dependencies")
                .get("href")
                .getAsString()
                .replace("{?bootVersion}", "?bootVersion=" + version);
    }

    private Map<String, List<StarterInfo>> parseDependencies(JsonObject baseInfoJSON, JsonObject depJSON, Map<String, ? extends Dependency> existsDependencyDB) {
        Gson gson = new Gson();
        Map<String, List<StarterInfo>> modules = new LinkedHashMap<>();
        // 设置仓库信息的id
        InitializrResponse depResponse = gson.fromJson(depJSON, InitializrResponse.class);
        depResponse.getRepositories().forEach((id, repository) -> repository.setId(id));

        JsonArray modulesJSON = baseInfoJSON.getAsJsonObject("dependencies").getAsJsonArray("values");
        for (JsonElement moduleEle : modulesJSON) {
            JsonObject module = moduleEle.getAsJsonObject();
            JsonArray dependenciesJSON = module.getAsJsonArray("values");
            List<StarterInfo> dependencies = new ArrayList<>(dependenciesJSON.size());
            for (JsonElement depEle : dependenciesJSON) {
                StarterInfo starterInfo = gson.fromJson(depEle.getAsJsonObject(), StarterInfo.class);

                InitializrDependency dependency = depResponse.getDependencies().get(starterInfo.getId());
                if (dependency != null) {
                    starterInfo.setGroupId(dependency.getGroupId());
                    starterInfo.setArtifactId(dependency.getArtifactId());
                    starterInfo.setVersion(dependency.getVersion());
                    starterInfo.setScope(Arrays.stream(DependencyScope.values())
                            .filter(scopeEnum -> scopeEnum.getScope().equals(dependency.getScope()))
                            .findFirst().orElse(DependencyScope.COMPILE));

                    InitializrBom bom = depResponse.getBoms().get(dependency.getBom());
                    if (bom != null) {
                        starterInfo.setBom(bom);
                        bom.getRepositories().forEach(rid -> starterInfo.addRepository(depResponse.getRepositories().get(rid)));
                    }
                    dependencies.add(starterInfo);
                }

                if (existsDependencyDB.containsKey(starterInfo.point())) {
                    starterInfo.setExist(true);
                }

            }

            modules.put(module.get("name").getAsString(), dependencies);
        }

        return modules;
    }
}
