package io.github.hdzitao.editstarters.buildsystem.gradle

import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.externalSystem.model.ProjectKeys
import com.intellij.openapi.externalSystem.model.project.LibraryData
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import io.github.hdzitao.editstarters.buildsystem.BuildSystem
import io.github.hdzitao.editstarters.dependency.Dependency
import io.github.hdzitao.editstarters.ui.ShowErrorException
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.plugins.gradle.util.GradleConstants
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile

/**
 * gradle构建系统
 *
 * @version 3.2.0
 */
class GradleBuildSystem(buildGradle: AbstractBuildGradle<*, *>, dependencies: List<Dependency>) :
    BuildSystem(buildGradle, dependencies) {

    companion object {
        /**
         * 根据文件名构建gradle build system
         */
        @JvmStatic
        fun from(context: DataContext): GradleBuildSystem {
            val psiFile = context.getData(CommonDataKeys.PSI_FILE)
            val project = context.getData(CommonDataKeys.PROJECT)
            if (psiFile == null || project == null) {
                throw ShowErrorException.internal()
            }

            val name = psiFile.name
            val buildGradle: AbstractBuildGradle<*, *> =
                when (name) {
                    GradleConstants.DEFAULT_SCRIPT_NAME -> BuildGradle(project, psiFile as GroovyFile)
                    GradleConstants.KOTLIN_DSL_SCRIPT_NAME -> BuildGradleKts(project, psiFile as KtFile)
                    else -> throw ShowErrorException("Not support extension!")
                }
            if (project.basePath.isNullOrBlank()) {
                throw ShowErrorException.internal()
            }

            val projectData = ExternalSystemApiUtil.findProjectNode(
                project, GradleConstants.SYSTEM_ID, project.basePath!!
            ) ?: throw ShowErrorException.internal()

            val dependencies = projectData.children
                .filter { ProjectKeys.LIBRARY == it.key }
                .map { it.data as LibraryData }
                .map { Dependency(it.groupId, it.artifactId, it.version) }
                .toList()

            return GradleBuildSystem(buildGradle, dependencies)
        }
    }
}