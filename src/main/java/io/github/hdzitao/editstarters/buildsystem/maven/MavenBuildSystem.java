package io.github.hdzitao.editstarters.buildsystem.maven;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import io.github.hdzitao.editstarters.buildsystem.BuildSystem;
import io.github.hdzitao.editstarters.dependency.Dependency;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * maven构建系统
 *
 * @version 3.2.0
 */
public class MavenBuildSystem extends BuildSystem {

    private MavenBuildSystem(PomXml pomXml, List<Dependency> dependencies) {
        super(pomXml, dependencies);
    }

    /**
     * 构建 maven build system
     */
    public static MavenBuildSystem from(DataContext context) {
        PsiFile psiFile = Objects.requireNonNull(context.getData(CommonDataKeys.PSI_FILE));
        PomXml pomXml = new PomXml((XmlFile) psiFile);
        MavenProject mavenProject = Objects.requireNonNull(MavenActionUtil.getMavenProject(context));
        List<Dependency> dependencies = mavenProject.getDependencies().stream()
                .map(d -> new Dependency(d.getGroupId(), d.getArtifactId(), d.getBaseVersion()))
                .collect(Collectors.toList());
        return new MavenBuildSystem(pomXml, dependencies);
    }
}