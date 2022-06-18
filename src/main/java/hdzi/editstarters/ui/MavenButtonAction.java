package hdzi.editstarters.ui;

import com.intellij.openapi.actionSystem.DataContext;
import hdzi.editstarters.buildsystem.BuildSystem;
import hdzi.editstarters.buildsystem.maven.MavenBuildSystem;
import org.jetbrains.idea.maven.model.MavenConstants;

/**
 * maven项目Edit Starters按钮
 */
public class MavenButtonAction extends EditStartersButtonAction {

    @Override
    protected boolean isMatchFile(String name) {
        return MavenConstants.POM_XML.equals(name);
    }

    @Override
    protected BuildSystem newBuildSystem(DataContext dataContext) {
        return MavenBuildSystem.of(dataContext);
    }
}