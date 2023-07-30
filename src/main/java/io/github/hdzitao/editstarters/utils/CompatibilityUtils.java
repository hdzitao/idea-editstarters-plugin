package io.github.hdzitao.editstarters.utils;

import com.intellij.openapi.externalSystem.model.DataNode;
import com.intellij.openapi.externalSystem.model.ExternalProjectInfo;
import com.intellij.openapi.externalSystem.model.ProjectSystemId;
import com.intellij.openapi.externalSystem.model.project.ProjectData;
import com.intellij.openapi.externalSystem.service.project.ProjectDataManager;
import com.intellij.openapi.externalSystem.settings.AbstractExternalSystemSettings;
import com.intellij.openapi.externalSystem.settings.ExternalProjectSettings;
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil;
import com.intellij.openapi.project.Project;

/**
 * 兼容性工具
 *
 * @version 3.2.0
 */
public final class CompatibilityUtils {
    private CompatibilityUtils() {
    }

    /**
     * 兼容 ExternalSystemApiUtil.findProjectData
     *
     * @param project
     * @param systemId
     * @param projectPath
     * @return
     */
    public static DataNode<ProjectData> findProjectData(Project project, ProjectSystemId systemId, String projectPath) {
        AbstractExternalSystemSettings<?, ?, ?> settings = ExternalSystemApiUtil.getSettings(project, systemId);
        ExternalProjectSettings linkedProjectSettings = settings.getLinkedProjectSettings(projectPath);
        if (linkedProjectSettings == null) return null;
        String rootProjectPath = linkedProjectSettings.getExternalProjectPath();
        ExternalProjectInfo projectInfo = ProjectDataManager.getInstance().getExternalProjectData(project, systemId, rootProjectPath);
        if (projectInfo == null) return null;
        return projectInfo.getExternalProjectStructure();
    }
}
