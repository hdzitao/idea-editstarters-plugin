package hdzi.editstarters.buildsystem.maven;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import hdzi.editstarters.EditStarters;
import hdzi.editstarters.buildsystem.BuildSystem;
import hdzi.editstarters.dependency.Dependency;
import hdzi.editstarters.ui.ShowErrorException;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by taojinhou on 2019/1/11.
 */
//@SuppressWarnings("ConstantConditions")
public class MavenBuildSystem extends BuildSystem {


    private MavenBuildSystem(DataContext context, List<Dependency> dependencies, EditStarters editStarters) {
        super(context, dependencies, editStarters);
    }

    public static MavenBuildSystem of(DataContext context) {
        PsiFile psiFile = context.getData(CommonDataKeys.PSI_FILE);
        if (!(psiFile instanceof XmlFile)) {
            throw new ShowErrorException("Not an XML file!");
        }
        PomXml pomXml = new PomXml((XmlFile) psiFile);
        MavenProject mavenProject = MavenActionUtil.getMavenProject(context);
        if (mavenProject == null) {
            throw new ShowErrorException("Not a maven project!");
        }
        List<Dependency> dependencies = mavenProject.getDependencies().stream()
                .map(d -> new Dependency(d.getGroupId(), d.getArtifactId(), d.getVersion()))
                .collect(Collectors.toList());
        return new MavenBuildSystem(context, dependencies, pomXml);
    }
}