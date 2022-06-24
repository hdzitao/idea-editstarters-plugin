package hdzi.editstarters.buildsystem.gradle;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.externalSystem.model.DataNode;
import com.intellij.openapi.externalSystem.model.ProjectKeys;
import com.intellij.openapi.externalSystem.model.project.LibraryData;
import com.intellij.openapi.externalSystem.model.project.ProjectData;
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import hdzi.editstarters.EditStarters;
import hdzi.editstarters.buildsystem.BuildSystem;
import hdzi.editstarters.buildsystem.ProjectDependency;
import hdzi.editstarters.buildsystem.ProjectFile;
import hdzi.editstarters.ui.ShowErrorException;
import lombok.SneakyThrows;
import org.jetbrains.kotlin.psi.KtFile;
import org.jetbrains.plugins.gradle.util.GradleConstants;
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by taojinhou on 2019/1/14.
 */
@SuppressWarnings("ConstantConditions")
public class GradleBuildSystem extends BuildSystem {

    private GradleBuildSystem(DataContext context, List<ProjectDependency> dependencies, EditStarters editStarters) {
        super(context, dependencies, editStarters);
    }

    @SneakyThrows
    public static GradleBuildSystem of(DataContext context) {
        PsiFile psiFile = context.getData(CommonDataKeys.PSI_FILE);
        Project project = context.getData(CommonDataKeys.PROJECT);
        String name = psiFile.getName();
        ProjectFile<?> projectFile;
        switch (name) {
            case GradleConstants.DEFAULT_SCRIPT_NAME:
                projectFile = new BuildGradle(project, (GroovyFile) psiFile);
                break;
            case GradleConstants.KOTLIN_DSL_SCRIPT_NAME:
                projectFile = new BuildGradleKts(project, (KtFile) psiFile);
                break;
            default:
                throw new ShowErrorException("Not support extension!");
        }

        DataNode<ProjectData> projectData = ExternalSystemApiUtil.findProjectData(project, GradleConstants.SYSTEM_ID, project.getBasePath());
        List<ProjectDependency> dependencies = projectData.getChildren().stream()
                .filter(node -> ProjectKeys.LIBRARY.equals(node.getKey()))
                .map(node -> (LibraryData) node.getData())
                .map(lib -> new ProjectDependency(lib.getGroupId(), lib.getArtifactId(), lib.getVersion()))
                .collect(Collectors.toList());

        return new GradleBuildSystem(context, dependencies, projectFile);
    }
}