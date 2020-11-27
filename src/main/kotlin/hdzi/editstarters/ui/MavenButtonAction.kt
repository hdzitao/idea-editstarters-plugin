package hdzi.editstarters.ui

import com.intellij.openapi.actionSystem.AnActionEvent
import hdzi.editstarters.buildsystem.maven.MavenSpringBootEditor
import hdzi.editstarters.ui.dialog.ButtonAction
import org.jetbrains.idea.maven.model.MavenConstants

/**
 * Created by taojinhou on 2019/1/11.
 */
class MavenButtonAction : ButtonAction() {
    override fun isMatchFile(name: String?): Boolean = MavenConstants.POM_XML == name

    override fun invoke(e: AnActionEvent) {
        MavenSpringBootEditor(e.dataContext).edit()
    }
}