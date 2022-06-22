package hdzi.editstarters.initializr;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import hdzi.editstarters.dependency.DependencyScope;
import hdzi.editstarters.dependency.StarterInfo;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class StartSpringIO {
    @Data
    public static class MetaData {
        private Versions.Version version;
        private String dependenciesUrl;
        private Map<String, List<StarterInfo>> dependencies;

    }

    @Data
    public static class Dependencies {
        private Map<String, InitializrDependency> dependencies;
        private Map<String, InitializrRepository> repositories;
        private Map<String, InitializrBom> boms;
    }

    private MetaData metaData;

    private Dependencies dependencies;

    public String spliceMetadataLink(String url) {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        String metadataLink = "/metadata/client";

        if (url.endsWith(metadataLink)) {
            return url;
        }

        return url + metadataLink;
    }

    public void setMetaData(Versions.Version version, JsonObject metaDataJson) {
        Gson gson = new Gson();

        MetaData metaData = new MetaData();
        // 版本
        {
            metaData.version = version;
        }
        // 解析依赖地址
        {
            metaData.dependenciesUrl = metaDataJson.getAsJsonObject("_links")
                    .getAsJsonObject("dependencies")
                    .get("href")
                    .getAsString()
                    .replace("{?bootVersion}", "?bootVersion=" + version.toVersionID());
        }
        // 解析 dependencies
        {
            metaData.dependencies = new LinkedHashMap<>();
            JsonArray dependenciesJson = metaDataJson.getAsJsonObject("dependencies").getAsJsonArray("values");
            Type type = new TypeToken<List<StarterInfo>>() {
            }.getType();
            for (JsonElement element : dependenciesJson) {
                JsonObject dependencyJson = element.getAsJsonObject();
                metaData.dependencies.put(dependencyJson.get("name").getAsString(),
                        gson.fromJson(dependencyJson.get("values"), type));
            }
        }

        this.metaData = metaData;
    }

    public void setDependencies(JsonObject dependenciesJson) {
        Gson gson = new Gson();
        this.dependencies = gson.fromJson(dependenciesJson, Dependencies.class);
        this.dependencies.getRepositories().forEach((id, repository) -> repository.setId(id));
        // 组合 dependencies 和 metaData
        for (Map.Entry<String, List<StarterInfo>> entry : this.metaData.dependencies.entrySet()) {
            String name = entry.getKey();
            List<StarterInfo> infos = entry.getValue();
            for (StarterInfo starterInfo : infos) {
                InitializrDependency dependency = this.dependencies.getDependencies().get(starterInfo.getId());
                if (dependency != null) {
                    starterInfo.setGroupId(dependency.getGroupId());
                    starterInfo.setArtifactId(dependency.getArtifactId());
                    starterInfo.setVersion(dependency.getVersion());
                    starterInfo.setScope(DependencyScope.getByScope(dependency.getScope()));

                    InitializrBom bom = this.dependencies.getBoms().get(dependency.getBom());
                    if (bom != null) {
                        starterInfo.setBom(bom);
                        bom.getRepositories().forEach(rid -> starterInfo.addRepository(this.dependencies.getRepositories().get(rid)));
                    }
                }
            }
        }
    }

    public Map<String, List<StarterInfo>> getModules() {
        Map<String, List<StarterInfo>> modules = new LinkedHashMap<>(this.metaData.dependencies);
        // 删除无效项
        Versions.Version version = this.metaData.version;
        Iterator<Map.Entry<String, List<StarterInfo>>> moduleIterator = modules.entrySet().iterator();
        while (moduleIterator.hasNext()) {
            Map.Entry<String, List<StarterInfo>> entry = moduleIterator.next();
            String name = entry.getKey();
            List<StarterInfo> infos = entry.getValue();
            Iterator<StarterInfo> infoIterator = infos.iterator();
            while (infoIterator.hasNext()) {
                StarterInfo starterInfo = infoIterator.next();
                if (StringUtils.isBlank(starterInfo.getGroupId())) {
                    // 检查坐标
                    infoIterator.remove();
                } else if ((StringUtils.isNoneBlank(starterInfo.getVersionRange())
                        && !version.inRange(Versions.parseRange(starterInfo.getVersionRange())))) {
                    // 版本范围检查
                    infoIterator.remove();
                }
            }
            // 删空了,就删除module
            if (infos.isEmpty()) {
                moduleIterator.remove();
            }
        }

        return modules;
    }
}
