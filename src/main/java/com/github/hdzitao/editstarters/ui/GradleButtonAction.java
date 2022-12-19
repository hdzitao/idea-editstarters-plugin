package com.github.hdzitao.editstarters.ui;

import com.github.hdzitao.editstarters.buildsystem.BuildSystem;
import com.github.hdzitao.editstarters.buildsystem.gradle.GradleBuildSystem;
import com.intellij.openapi.actionSystem.DataContext;
import org.jetbrains.plugins.gradle.util.GradleConstants;

/**
 * Created by taojinhou on 2019/1/14.
 */
public class GradleButtonAction extends EditStartersButtonAction {
    @Override
    protected boolean isMatchFile(String name) {
        return GradleConstants.DEFAULT_SCRIPT_NAME.equals(name)
                || GradleConstants.KOTLIN_DSL_SCRIPT_NAME.equals(name);
    }

    @Override
    protected BuildSystem newBuildSystem(DataContext dataContext) {
        return GradleBuildSystem.of(dataContext);
    }
}