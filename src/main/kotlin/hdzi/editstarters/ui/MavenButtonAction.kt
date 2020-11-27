package hdzi.editstarters.ui

import com.intellij.openapi.actionSystem.AnActionEvent
import hdzi.editstarters.buildsystem.maven.MavenBuildSystem
import hdzi.editstarters.ui.dialog.EditStartersButtonAction
import org.jetbrains.idea.maven.model.MavenConstants

/**
 * maven项目Edit Starters按钮
 */
class MavenButtonAction : EditStartersButtonAction() {
    override fun String.isMatchFile() = this == MavenConstants.POM_XML

    override fun doAction(e: AnActionEvent) {
        MavenBuildSystem(e.dataContext).edit()
    }
}