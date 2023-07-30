package io.github.hdzitao.editstarters.ui;

import com.intellij.openapi.actionSystem.DataContext;
import io.github.hdzitao.editstarters.buildsystem.BuildSystem;
import io.github.hdzitao.editstarters.buildsystem.gradle.GradleBuildSystem;
import org.jetbrains.plugins.gradle.util.GradleConstants;

/**
 * gradle项目Edit Starters按钮
 *
 * @version 3.2.0
 */
public class GradleButtonAction extends EditStartersButtonAction {
    @Override
    protected boolean isMatched(String name) {
        return GradleConstants.DEFAULT_SCRIPT_NAME.equals(name)
                || GradleConstants.KOTLIN_DSL_SCRIPT_NAME.equals(name);
    }

    @Override
    protected BuildSystem newBuildSystem(DataContext dataContext) {
        return GradleBuildSystem.from(dataContext);
    }
}