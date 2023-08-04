package io.github.hdzitao.editstarters.buildsystem.maven;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import io.github.hdzitao.editstarters.buildsystem.BuildSystem;
import io.github.hdzitao.editstarters.dependency.Dependency;
import io.github.hdzitao.editstarters.ui.ShowErrorException;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * maven构建系统
 *
 * @version 3.2.0
 */
public class MavenBuildSystem extends BuildSystem {

    private MavenBuildSystem(DataContext context, PomXml pomXml, List<Dependency> dependencies) {
        super(context, pomXml, dependencies);
    }

    /**
     * 构建 maven build system
     *
     * @param context
     * @return
     */
    public static MavenBuildSystem from(DataContext context) {
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
                .map(d -> new Dependency(d.getGroupId(), d.getArtifactId(), d.getBaseVersion()))
                .collect(Collectors.toList());
        return new MavenBuildSystem(context, pomXml, dependencies);
    }
}