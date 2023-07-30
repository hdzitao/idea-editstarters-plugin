package io.github.hdzitao.editstarters.ui;

import com.intellij.openapi.actionSystem.DataContext;
import io.github.hdzitao.editstarters.buildsystem.BuildSystem;
import io.github.hdzitao.editstarters.buildsystem.maven.MavenBuildSystem;
import org.jetbrains.idea.maven.model.MavenConstants;

/**
 * maven项目Edit Starters按钮
 *
 * @version 3.2.0
 */
public class MavenButtonAction extends EditStartersButtonAction {

    @Override
    protected boolean isMatched(String name) {
        return MavenConstants.POM_XML.equals(name);
    }

    @Override
    protected BuildSystem newBuildSystem(DataContext dataContext) {
        return MavenBuildSystem.from(dataContext);
    }
}