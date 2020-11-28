package hdzi.editstarters.ui

import com.intellij.openapi.actionSystem.DataContext
import hdzi.editstarters.buildsystem.maven.MavenBuildSystem
import org.jetbrains.idea.maven.model.MavenConstants

/**
 * maven项目Edit Starters按钮
 */
class MavenButtonAction : EditStartersButtonAction() {
    override fun String.isMatchFile() = this == MavenConstants.POM_XML

    override fun newBuildSystem(dataContext: DataContext) = MavenBuildSystem(dataContext)

}