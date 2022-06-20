package hdzi.editstarters.initializr;

import com.google.gson.Gson;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import hdzi.editstarters.dependency.SpringBoot;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@State(name = "spring_boot_project_cache",
        storages = @Storage(value = "editstarters.xml", roamingType = RoamingType.DISABLED))
public class CachePersistentComponent implements PersistentStateComponent<CachePersistentComponent.State> {
    public static CachePersistentComponent getInstance(Project project) {
        return ServiceManager.getService(project, CachePersistentComponent.class);
    }

    @Data
    public static class State {
        private String projectJson;
        private String url;
        private String version;
        private Long createTime;
    }

    private State state;

    public SpringBoot get(String url, String version) {
        // 检查缓存
        if (this.state != null
                && Objects.equals(url, this.state.url)
                && Objects.equals(version, this.state.version)) {
            return new Gson().fromJson(this.state.projectJson, SpringBoot.class);
        }

        return null;
    }

    public void put(String url, String version, SpringBoot project) {
        if (this.state == null) {
            this.state = new State();
            this.state.createTime = System.currentTimeMillis();
        }
        this.state.url = url;
        this.state.version = version;
        this.state.projectJson = new Gson().toJson(project);
    }

    @Nullable
    @Override
    public CachePersistentComponent.State getState() {
        return this.state;
    }

    @Override
    public void loadState(@NotNull CachePersistentComponent.State state) {
        this.state = state;
    }
}
