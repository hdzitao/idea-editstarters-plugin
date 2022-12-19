package com.github.hdzitao.editstarters.buildsystem.maven;

import com.github.hdzitao.editstarters.EditStarters;
import com.github.hdzitao.editstarters.buildsystem.BuildSystem;
import com.github.hdzitao.editstarters.dependency.Dependency;
import com.github.hdzitao.editstarters.ui.ShowErrorException;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
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
                .map(d -> new Dependency(d.getGroupId(), d.getArtifactId(), d.getBaseVersion()))
                .collect(Collectors.toList());
        return new MavenBuildSystem(context, dependencies, pomXml);
    }
}