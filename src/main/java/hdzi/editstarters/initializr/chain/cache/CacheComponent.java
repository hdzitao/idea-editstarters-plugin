package hdzi.editstarters.initializr.chain.cache;

import com.google.gson.Gson;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import hdzi.editstarters.dependency.SpringBootProject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@State(name = "spring_boot_project_cache",
        storages = @Storage(value = "editstarters.xml", roamingType = RoamingType.DISABLED))
public class CacheComponent implements PersistentStateComponent<CacheComponent> {
    public static CacheComponent getInstance(Project project) {
        return ServiceManager.getService(project, CacheComponent.class);
    }

    private final Gson gson = new Gson();


    public final Map<String, String> projects = new ConcurrentHashMap<>();

    public SpringBootProject get(String url, String version) {
        return gson.fromJson(this.projects.get(key(url, version)), SpringBootProject.class);
    }

    public void put(String url, String version, SpringBootProject project) {
        this.projects.put(key(url, version), gson.toJson(project));
    }

    private String key(String url, String version) {
        return url + "::" + version;
    }

    @Nullable
    @Override
    public CacheComponent getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull CacheComponent component) {
        XmlSerializerUtil.copyBean(component, this);
    }
}
