package hdzi.editstarters.buildsystem.maven;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.psi.xml.XmlFile;
import hdzi.editstarters.buildsystem.BuildSystem;
import hdzi.editstarters.buildsystem.ProjectDependency;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

import java.util.stream.Collectors;

/**
 * Created by taojinhou on 2019/1/11.
 */

public class MavenBuildSystem extends BuildSystem {

    public MavenBuildSystem(DataContext context) {
        super(context,
                () -> new PomXml((XmlFile) context.getData(CommonDataKeys.PSI_FILE)),
                () -> MavenActionUtil.getMavenProject(context).getDependencies().stream()
                        .map(d -> new ProjectDependency(d.getGroupId(), d.getArtifactId(), d.getVersion()))
                        .collect(Collectors.toList()));
    }
}