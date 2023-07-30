package io.github.hdzitao.editstarters.initializr;

import com.intellij.openapi.project.Project;
import io.github.hdzitao.editstarters.buildsystem.BuildSystem;
import io.github.hdzitao.editstarters.version.Version;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Initializr参数
 *
 * @version 3.2.0
 */
@Getter
@Setter
@Accessors(chain = true)
public class InitializrParameter {
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
}