package hdzi.editstarters.initializr;

import com.google.gson.Gson;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.RoamingType;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import hdzi.editstarters.dependency.SpringBoot;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@State(name = "spring_boot_project_cache",
        storages = @Storage(value = "editstarters.xml", roamingType = RoamingType.DISABLED))
public class CachePersistentComponent implements PersistentStateComponent<CachePersistentComponent.State> {

    private final Gson gson = new Gson();

    public static CachePersistentComponent getInstance(Project project) {
        return project.getService(CachePersistentComponent.class);
    }

    @Getter
    @Setter
    public static class State {
        private String projectJson;
        private String url;
        private String version;
        private long updateTime;
    }

    private State state;

    public SpringBoot get(String url, String version) {
        // 检查缓存
        if (this.state != null
                && Objects.equals(url, this.state.url)
                && Objects.equals(version, this.state.version)) {
            return gson.fromJson(this.state.projectJson, SpringBoot.class);
        }

        return null;
    }

    public void put(String url, String version, SpringBoot project) {
        if (this.state == null) {
            this.state = new State();
        }
        this.state.url = url;
        this.state.version = version;
        this.state.projectJson = gson.toJson(project);
        this.state.updateTime = System.currentTimeMillis();
    }

    public String getUrl() {
        if (this.state != null && StringUtils.isNoneBlank(this.state.url)) {
            return this.state.url;
        }

        return "https://start.spring.io/";
    }

    public long getUpdateTime() {
        if (this.state != null) {
            return this.state.updateTime;
        }

        return 0L;
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
