package hdzi.editstarters.ui

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import hdzi.editstarters.gradle.GradleSpringBootEditor
import hdzi.editstarters.ui.dialog.ExceptionAction
import org.jetbrains.plugins.gradle.util.GradleConstants

/**
 * Created by taojinhou on 2019/1/14.
 */
class GradleButtonAction : ExceptionAction() {
    override fun invoke(e: AnActionEvent) {
        GradleSpringBootEditor(e.dataContext).edit()
    }

    override fun update(e: AnActionEvent) {
        val name = e.getData(DataKeys.PSI_FILE)?.name

        e.presentation.isEnabled =
                GradleConstants.DEFAULT_SCRIPT_NAME == name ||
                GradleConstants.KOTLIN_DSL_SCRIPT_NAME == name
    }
}