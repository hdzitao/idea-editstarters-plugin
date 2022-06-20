package hdzi.editstarters.initializr.chain.cache;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import hdzi.editstarters.dependency.SpringBootProject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@State(name = "spring_boot_project_cache",
        storages = @Storage(value = "editstarters.xml", roamingType = RoamingType.DISABLED))
public class CacheComponent implements PersistentStateComponent<Map<String, SpringBootProject>> {
    public static CacheComponent getInstance(Project project) {
        return ServiceManager.getService(project, CacheComponent.class);
    }


    public final Map<String, SpringBootProject> projects = new ConcurrentHashMap<>();

    public SpringBootProject get(String url, String version) {
        return this.projects.get(key(url, version));
    }

    public void put(String url, String version, SpringBootProject project) {
        this.projects.put(key(url, version), project);
    }

    private String key(String url, String version) {
        return url + "::" + version;
    }

    @Nullable
    @Override
    public Map<String, SpringBootProject> getState() {
        return projects;
    }

    @Override
    public void loadState(@NotNull Map<String, SpringBootProject> state) {
        this.projects.putAll(state);
    }
}
