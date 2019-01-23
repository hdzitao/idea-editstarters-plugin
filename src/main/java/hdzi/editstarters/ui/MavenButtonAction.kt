package hdzi.editstarters.ui

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import hdzi.editstarters.maven.MavenSpringBootEditor
import hdzi.editstarters.ui.dialog.ExceptionAction
import org.jetbrains.idea.maven.model.MavenConstants

/**
 * Created by taojinhou on 2019/1/11.
 */
class MavenButtonAction : ExceptionAction() {
    override fun invoke(e: AnActionEvent) {
        MavenSpringBootEditor(e.dataContext).edit()
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled =
                MavenConstants.POM_XML == e.getData(DataKeys.PSI_FILE)?.name
    }
}