package hdzi.editstarters.ui

import com.intellij.openapi.actionSystem.AnActionEvent
import hdzi.editstarters.maven.MavenSpringBootEditor
import hdzi.editstarters.ui.dialog.EditButtonAction
import org.jetbrains.idea.maven.model.MavenConstants

/**
 * Created by taojinhou on 2019/1/11.
 */
class MavenButtonAction : EditButtonAction() {
    override fun isMatchFile(name: String?): Boolean = MavenConstants.POM_XML == name

    override fun invoke(e: AnActionEvent) {
        MavenSpringBootEditor(e.dataContext).edit()
    }
}