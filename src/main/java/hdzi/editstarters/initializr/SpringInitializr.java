package hdzi.editstarters.initializr;

import com.google.gson.*;
import com.intellij.util.io.HttpRequests;
import hdzi.editstarters.dependency.Dependency;
import hdzi.editstarters.dependency.DependencyScope;
import hdzi.editstarters.ui.ShowErrorException;
import lombok.Data;

import java.io.IOException;
import java.util.*;

/**
 * Created by taojinhou on 2018/12/21.
 */
@Data
public class SpringInitializr {
    private final String url;
    private final String bootVersion;
    private final String currentVersionID;
    private final Map<String, List<StarterInfo>> modulesMap = new LinkedHashMap<>();
    private final Map<String, StarterInfo> pointMap = new LinkedHashMap<>();
    private InitializrVersion version;
    Set<StarterInfo> existStarters = new LinkedHashSet<>();


    public SpringInitializr(String url, String bootVersion) {
        this.url = url;
        this.bootVersion = bootVersion;
        this.currentVersionID = this.bootVersion.replaceFirst("^(\\d+\\.\\d+\\.\\d+).*$", "$1");

        try {
            Gson gson = new Gson();
            JsonObject baseInfoJSON = HttpRequests.request(this.url).accept("application/json").connect(request ->
                    gson.fromJson(request.readString(), JsonObject.class));

            this.version = gson.fromJson(baseInfoJSON.getAsJsonObject("bootVersion"), InitializrVersion.class);
            String dependenciesUrl = parseDependenciesUrl(baseInfoJSON, this.currentVersionID);
            JsonObject depsJSON = HttpRequests.request(dependenciesUrl).connect(request ->
                    gson.fromJson(request.readString(), JsonObject.class));
            parseDependencies(baseInfoJSON, depsJSON);
        } catch (IOException ignore) {
            throw new ShowErrorException("Request failure! Your spring boot version may not be supported, please confirm.");
        } catch (JsonSyntaxException ignore) {
            throw new ShowErrorException("Request failure! JSON syntax error for response, please confirm.");
        }
    }

    public void addExistsStarter(Dependency depend) {
        StarterInfo starterInfo = this.pointMap.get(depend.point());
        if (starterInfo != null) {
            starterInfo.setExist(true);
            this.existStarters.add(starterInfo);
        }
    }

    private String parseDependenciesUrl(JsonObject json, String version) {
        return json.getAsJsonObject("_links")
                .getAsJsonObject("dependencies")
                .get("href")
                .getAsString()
                .replace("{?bootVersion}", "?bootVersion=" + version);
    }


    private void parseDependencies(JsonObject baseInfoJSON, JsonObject depJSON) {
        Gson gson = new Gson();
        // 设置仓库信息的id
        InitializrResponse depResponse = gson.fromJson(depJSON, InitializrResponse.class);
        depResponse.repositories.forEach((id, repository) -> repository.setId(id));

        JsonArray modulesJSON = baseInfoJSON.getAsJsonObject("dependencies").getAsJsonArray("values");
        for (JsonElement moduleEle : modulesJSON) {
            JsonObject module = moduleEle.getAsJsonObject();
            JsonArray dependenciesJSON = module.getAsJsonArray("values");
            List<StarterInfo> dependencies = new ArrayList<>(dependenciesJSON.size());
            for (JsonElement depEle : dependenciesJSON) {
                StarterInfo starterInfo = gson.fromJson(depEle.getAsJsonObject(), StarterInfo.class);

                InitializrDependency dependency = depResponse.dependencies.get(starterInfo.getId());
                if (dependency != null) {
                    starterInfo.setGroupId(dependency.getGroupId());
                    starterInfo.setArtifactId(dependency.getArtifactId());
                    starterInfo.setVersion(dependency.getVersion());
                    starterInfo.setScope(Arrays.stream(DependencyScope.values())
                            .filter(scopeEnum -> scopeEnum.getScope().equals(dependency.getScope()))
                            .findFirst().orElse(DependencyScope.COMPILE));

                    InitializrBom bom = depResponse.boms.get(dependency.getBom());
                    if (bom != null) {
                        starterInfo.setBom(bom);
                        bom.getRepositories().forEach(rid -> starterInfo.addRepository(depResponse.repositories.get(rid)));
                    }

                    this.pointMap.put(starterInfo.point(), starterInfo);
                    dependencies.add(starterInfo);
                }

            }

            this.modulesMap.put(module.get("name").getAsString(), dependencies);
        }
    }
}
