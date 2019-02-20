package hdzi.editstarters.ui

import com.intellij.openapi.actionSystem.AnActionEvent
import hdzi.editstarters.gradle.GradleSpringBootEditor
import hdzi.editstarters.ui.dialog.ButtonAction
import org.jetbrains.plugins.gradle.util.GradleConstants

/**
 * Created by taojinhou on 2019/1/14.
 */
class GradleButtonAction : ButtonAction() {
    override fun isMatchFile(name: String?): Boolean =
        GradleConstants.DEFAULT_SCRIPT_NAME == name || GradleConstants.KOTLIN_DSL_SCRIPT_NAME == name

    override fun invoke(e: AnActionEvent) {
        GradleSpringBootEditor(e.dataContext).edit()
    }
}