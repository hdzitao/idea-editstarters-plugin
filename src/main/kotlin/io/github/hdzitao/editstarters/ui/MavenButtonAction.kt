package io.github.hdzitao.editstarters.ui

import com.intellij.openapi.actionSystem.DataContext
import io.github.hdzitao.editstarters.buildsystem.BuildSystem
import io.github.hdzitao.editstarters.buildsystem.maven.MavenBuildSystem.Companion.from
import org.jetbrains.idea.maven.model.MavenConstants

/**
 * maven项目Edit Starters按钮
 *
 * @version 3.2.0
 */
class MavenButtonAction : EditStartersButtonAction() {
    override fun isMatched(name: String): Boolean {
        return MavenConstants.POM_XML == name
    }

    override fun newBuildSystem(dataContext: DataContext): BuildSystem {
        return from(dataContext)
    }
}