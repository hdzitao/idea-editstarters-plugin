package hdzi.editstarters.ui

import com.intellij.openapi.actionSystem.DataContext
import hdzi.editstarters.buildsystem.gradle.GradleBuildSystem
import hdzi.editstarters.ui.dialog.EditStartersButtonAction
import org.jetbrains.plugins.gradle.util.GradleConstants

/**
 * Created by taojinhou on 2019/1/14.
 */
class GradleButtonAction : EditStartersButtonAction() {
    override fun String.isMatchFile(): Boolean =
        this == GradleConstants.DEFAULT_SCRIPT_NAME || this == GradleConstants.KOTLIN_DSL_SCRIPT_NAME

    override fun newBuildSystem(dataContext: DataContext) = GradleBuildSystem(dataContext)
}