package io.github.hdzitao.editstarters.initializr;

import com.intellij.openapi.project.Project;
import io.github.hdzitao.editstarters.buildsystem.BuildSystem;
import io.github.hdzitao.editstarters.ohub.OHub;
import io.github.hdzitao.editstarters.version.Version;

/**
 * Initializr参数
 *
 * @version 3.2.0
 */
public class InitializrRequest {
    /**
     * 项目
     */
    private Project project;

    /**
     * 构建系统
     */
    private BuildSystem buildSystem;

    /**
     * start.spring.io 地址
     */
    private String url;

    /**
     * spring boot 版本
     */
    private Version version;

    /**
     * 启用缓存
     */
    private boolean enableCache = true;

    /**
     * 支持的接版本
     */
    private OHub[] supportedOHubs;

    /**
     * 选择的旧版本
     */
    private OHub selectedOHub;

    /**
     * 初始化链
     */
    private InitializrChain chain;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public BuildSystem getBuildSystem() {
        return buildSystem;
    }

    public void setBuildSystem(BuildSystem buildSystem) {
        this.buildSystem = buildSystem;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public boolean isEnableCache() {
        return enableCache;
    }

    public void setEnableCache(boolean enableCache) {
        this.enableCache = enableCache;
    }

    public OHub[] getSupportedOHubs() {
        return supportedOHubs;
    }

    public void setSupportedOHubs(OHub[] supportedOHubs) {
        this.supportedOHubs = supportedOHubs;
    }

    public OHub getSelectedOHub() {
        return selectedOHub;
    }

    public void setSelectedOHub(OHub selectedOHub) {
        this.selectedOHub = selectedOHub;
    }

    public InitializrChain getChain() {
        return chain;
    }

    public void setChain(InitializrChain chain) {
        this.chain = chain;
    }
}
