package hdzi.editstarters.initializr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import hdzi.editstarters.dependency.IBom;
import hdzi.editstarters.dependency.IRepository;
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

    public static CachePersistentComponent getInstance(Project project) {
        return ServiceManager.getService(project, CachePersistentComponent.class);
    }

    @Getter
    @Setter
    public static class State {
        private String projectJson;
        private String url;
        private String version;
        private Long updateTime;
    }

    private State state;

    public SpringBoot get(String url, String version) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(IBom.class, (JsonDeserializer<IBom>) (element, type, context) ->
                context.deserialize(element, InitializrBom.class));
        gsonBuilder.registerTypeAdapter(IRepository.class, (JsonDeserializer<IRepository>) (element, type, context) ->
                context.deserialize(element, InitializrRepository.class));
        Gson gson = gsonBuilder.create();
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
        this.state.projectJson = new Gson().toJson(project);
        this.state.updateTime = System.currentTimeMillis();
    }

    public String getUrl() {
        if (this.state != null && StringUtils.isNoneBlank(this.state.url)) {
            return this.state.url;
        }

        return "https://start.spring.io/";
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
