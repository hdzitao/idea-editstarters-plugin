package hdzi.editstarters.gradle

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import hdzi.editstarters.springboot.ProjectDependGetter
import hdzi.editstarters.springboot.bean.ProjectDependency
import org.gradle.tooling.model.idea.IdeaProject
import org.gradle.tooling.model.idea.IdeaSingleEntryLibraryDependency
import org.jetbrains.plugins.gradle.service.execution.GradleExecutionHelper
import org.jetbrains.plugins.gradle.settings.GradleExecutionSettings
import org.jetbrains.plugins.gradle.util.GradleConstants

class GradleDependGetter : ProjectDependGetter {
    override fun get(context: DataContext): List<ProjectDependency> {
        val project = context.getData(DataKeys.PROJECT)!!
        val basePath = context.getData(DataKeys.VIRTUAL_FILE)!!.parent!!.path
        val setting: GradleExecutionSettings = ExternalSystemApiUtil.getExecutionSettings(
            project,
            basePath,
            GradleConstants.SYSTEM_ID
        )
        val helper = GradleExecutionHelper()

        return helper.execute(basePath, setting) { connect ->
            val ideaModule = connect.getModel(IdeaProject::class.java).modules.getAt(0)
            ideaModule.dependencies
                .filter { it is IdeaSingleEntryLibraryDependency && it.gradleModuleVersion != null }
                .map {
                    val moduleVersion = (it as IdeaSingleEntryLibraryDependency).gradleModuleVersion!!
                    ProjectDependency(moduleVersion.group, moduleVersion.name, moduleVersion.version)
                }
        }
    }
}
