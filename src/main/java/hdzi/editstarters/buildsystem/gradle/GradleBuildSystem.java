package hdzi.editstarters.buildsystem.gradle;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.psi.PsiFile;
import hdzi.editstarters.buildsystem.BuildSystem;
import hdzi.editstarters.buildsystem.ProjectDependency;
import hdzi.editstarters.ui.ShowErrorException;
import org.gradle.plugins.ide.idea.model.IdeaModule;
import org.gradle.tooling.model.GradleModuleVersion;
import org.gradle.tooling.model.idea.IdeaProject;
import org.gradle.tooling.model.idea.IdeaSingleEntryLibraryDependency;
import org.jetbrains.kotlin.psi.KtFile;
import org.jetbrains.plugins.gradle.service.execution.GradleExecutionHelper;
import org.jetbrains.plugins.gradle.settings.GradleExecutionSettings;
import org.jetbrains.plugins.gradle.util.GradleConstants;
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by taojinhou on 2019/1/14.
 */
public class GradleBuildSystem extends BuildSystem {
    public GradleBuildSystem(DataContext context) {
        super(context,
                () -> {
                    PsiFile psiFile = context.getData(CommonDataKeys.PSI_FILE);
                    String name = psiFile.getName();
                    switch (name) {
                        case GradleConstants.DEFAULT_SCRIPT_NAME:
                            return new BuildGradle(context.getData(CommonDataKeys.PROJECT), (GroovyFile) psiFile);
                        case GradleConstants.KOTLIN_DSL_SCRIPT_NAME:
                            return new BuildGradleKts(context.getData(CommonDataKeys.PROJECT), (KtFile) psiFile);
                        default:
                            throw new ShowErrorException("Not support extension!");
                    }
                },
                () -> {
                    Project project = context.getData(CommonDataKeys.PROJECT);
                    String basePath = context.getData(CommonDataKeys.VIRTUAL_FILE).getParent().getPath();
                    GradleExecutionSettings setting = ExternalSystemApiUtil.getExecutionSettings(project, basePath, GradleConstants.SYSTEM_ID);
                    ProgressManager progressManager = ProgressManager.getInstance();
                    try {
                        return progressManager.runProcessWithProgressSynchronously((ThrowableComputable<List<ProjectDependency>, Exception>) () -> {
                            progressManager.getProgressIndicator().setIndeterminate(true);
                            return new GradleExecutionHelper().execute(basePath, setting, connect -> {
                                IdeaModule ideaModule = (IdeaModule) connect.getModel(IdeaProject.class).getModules().getAt(0);
                                return ideaModule.resolveDependencies().stream()
                                        .filter(it -> it instanceof IdeaSingleEntryLibraryDependency
                                                && ((IdeaSingleEntryLibraryDependency) it).getGradleModuleVersion() != null)
                                        .map(it -> {
                                            GradleModuleVersion moduleVersion = ((IdeaSingleEntryLibraryDependency) it).getGradleModuleVersion();
                                            return new ProjectDependency(moduleVersion.getGroup(), moduleVersion.getName(), moduleVersion.getVersion());
                                        }).collect(Collectors.toList());
                            });
                        }, "Load Gradle Project", false, project);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}