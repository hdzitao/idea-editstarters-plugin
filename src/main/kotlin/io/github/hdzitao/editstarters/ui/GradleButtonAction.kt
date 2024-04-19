package io.github.hdzitao.editstarters.ui

import com.intellij.openapi.actionSystem.DataContext
import io.github.hdzitao.editstarters.buildsystem.BuildSystem
import io.github.hdzitao.editstarters.buildsystem.gradle.GradleBuildSystem.Companion.from
import org.jetbrains.plugins.gradle.util.GradleConstants

/**
 * gradle项目Edit Starters按钮
 *
 * @version 3.2.0
 */
class GradleButtonAction : EditStartersButtonAction() {
    override fun isMatched(name: String): Boolean {
        return GradleConstants.DEFAULT_SCRIPT_NAME == name || GradleConstants.KOTLIN_DSL_SCRIPT_NAME == name
    }

    override fun newBuildSystem(dataContext: DataContext): BuildSystem {
        return from(dataContext)
    }
}