package io.github.hdzitao.editstarters.buildsystem.gradle;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.externalSystem.model.DataNode;
import com.intellij.openapi.externalSystem.model.ProjectKeys;
import com.intellij.openapi.externalSystem.model.project.LibraryData;
import com.intellij.openapi.externalSystem.model.project.ProjectData;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import io.github.hdzitao.editstarters.buildsystem.BuildSystem;
import io.github.hdzitao.editstarters.dependency.Dependency;
import io.github.hdzitao.editstarters.ui.ShowErrorException;
import io.github.hdzitao.editstarters.utils.CompatibilityUtils;
import lombok.SneakyThrows;
import org.jetbrains.kotlin.psi.KtFile;
import org.jetbrains.plugins.gradle.util.GradleConstants;
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * gradle构建系统
 *
 * @version 3.2.0
 */
public class GradleBuildSystem extends BuildSystem {

    private GradleBuildSystem(AbstractBuildGradle<?> buildGradle, List<Dependency> dependencies) {
        super(buildGradle, dependencies);
    }

    /**
     * 根据文件名构建gradle build system
     */
    @SneakyThrows
    public static GradleBuildSystem from(DataContext context) {
        PsiFile psiFile = context.getData(CommonDataKeys.PSI_FILE);
        Project project = context.getData(CommonDataKeys.PROJECT);
        if (psiFile == null || project == null) {
            throw ShowErrorException.internal();
        }

        String name = psiFile.getName();
        AbstractBuildGradle<?> buildGradle;
        switch (name) {
            case GradleConstants.DEFAULT_SCRIPT_NAME:
                buildGradle = new BuildGradle(project, (GroovyFile) psiFile);
                break;
            case GradleConstants.KOTLIN_DSL_SCRIPT_NAME:
                buildGradle = new BuildGradleKts(project, (KtFile) psiFile);
                break;
            default:
                throw new ShowErrorException("Not support extension!");
        }

        DataNode<ProjectData> projectData = CompatibilityUtils.findProjectData(project, GradleConstants.SYSTEM_ID, project.getBasePath());
        if (projectData == null) {
            throw ShowErrorException.internal();
        }
        List<Dependency> dependencies = projectData.getChildren().stream()
                .filter(node -> ProjectKeys.LIBRARY.equals(node.getKey()))
                .map(node -> (LibraryData) node.getData())
                .map(lib -> new Dependency(lib.getGroupId(), lib.getArtifactId(), lib.getVersion()))
                .collect(Collectors.toList());

        return new GradleBuildSystem(buildGradle, dependencies);
    }
}