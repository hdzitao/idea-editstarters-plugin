package io.github.hdzitao.editstarters.cache;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.RoamingType;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.annotations.OptionTag;
import io.github.hdzitao.editstarters.springboot.SpringBoot;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * 缓存
 *
 * @version 3.2.0
 */
@State(name = "editstarters-initializr-cache",
        storages = @Storage(value = "editstarters/initializr-cache.xml", roamingType = RoamingType.DISABLED))
public class InitializrCache implements PersistentStateComponent<InitializrCache.State> {

    public static InitializrCache getInstance(Project project) {
        return project.getService(InitializrCache.class);
    }

    @Getter
    @Setter
    public static class State {
        @OptionTag(converter = SpringBootConverter.class)
        private SpringBoot springBoot;
        private String url;
        private String version;
        private long updateTime;
    }

    public static class SpringBootConverter extends JSONConverter<SpringBoot> {
    }


    private State state;

    public SpringBoot get(String url, String version) {
        // 检查缓存
        if (state != null
                && Objects.equals(url, state.url)
                && Objects.equals(version, state.version)) {
            return state.springBoot;
        }

        return null;
    }

    public void put(String url, String version, SpringBoot project) {
        if (state == null) {
            state = new State();
        }
        state.url = url;
        state.version = version;
        state.springBoot = project;
        state.updateTime = System.currentTimeMillis();
    }

    public String getUrl() {
        if (state != null && StringUtils.isNoneBlank(state.url)) {
            return state.url;
        }

        return "https://start.spring.io/";
    }

    public long getUpdateTime() {
        if (state != null) {
            return state.updateTime;
        }

        return 0L;
    }

    @Nullable
    @Override
    public InitializrCache.State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull InitializrCache.State state) {
        this.state = state;
    }

}
