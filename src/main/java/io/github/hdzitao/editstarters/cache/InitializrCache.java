package io.github.hdzitao.editstarters.cache;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.RoamingType;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.annotations.OptionTag;
import io.github.hdzitao.editstarters.ohub.GitHub;
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

    /**
     * 缓存bean
     */
    @Getter
    @Setter
    public static class State {
        @OptionTag(converter = SpringBootConverter.class)
        private SpringBoot springBoot;
        private String url;
        private String oHub;
        private String version;
        private long updateTime;
    }

    public static class SpringBootConverter extends JSONConverter<SpringBoot> {
    }

    private State state;

    /**
     * 缓存是否有效
     */
    public boolean enable() {
        return state != null && StringUtils.isNoneBlank(state.url);
    }

    /**
     * 初始化
     */
    public void initialize() {
        if (!enable()) {
            state = new State();
        }
    }

    /**
     * 获取缓存
     */
    public SpringBoot getSpringBoot(String url, String version) {
        // 检查缓存
        if (enable()
                && Objects.equals(url, state.url)
                && Objects.equals(version, state.version)
                && state.springBoot != null) {
            return state.springBoot;
        }

        return null;
    }

    /**
     * 更新缓存
     */
    public void putSpringBoot(String url, String version, SpringBoot project) {
        state.url = url;
        state.version = version;
        state.springBoot = project;
        state.updateTime = System.currentTimeMillis();
    }

    /**
     * 获取url
     */
    public String getUrl() {
        if (enable() && StringUtils.isNotEmpty(state.url)) {
            return state.url;
        }

        return "https://start.spring.io/";
    }

    /**
     * 获取更新时间
     */
    public long getUpdateTime() {
        if (enable()) {
            return state.updateTime;
        }

        return 0L;
    }

    /**
     * 获取ohub
     */
    public String getOHubName() {
        if (enable() && StringUtils.isNotEmpty(state.oHub)) {
            return state.oHub;
        }

        return GitHub.NAME;
    }

    /**
     * 缓存oHub
     */
    public void putOHubName(String oHubName) {
        state.oHub = oHubName;
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
