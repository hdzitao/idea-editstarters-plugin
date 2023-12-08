package io.github.hdzitao.editstarters.utils;

import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.externalSystem.model.DataNode;
import com.intellij.openapi.externalSystem.model.ExternalProjectInfo;
import com.intellij.openapi.externalSystem.model.ProjectSystemId;
import com.intellij.openapi.externalSystem.model.project.ProjectData;
import com.intellij.openapi.externalSystem.service.project.ProjectDataManager;
import com.intellij.openapi.externalSystem.settings.AbstractExternalSystemSettings;
import com.intellij.openapi.externalSystem.settings.ExternalProjectSettings;
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil;
import com.intellij.openapi.project.Project;

import java.lang.reflect.Method;

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
     */
    public static DataNode<ProjectData> findProjectData(Project project, ProjectSystemId systemId, String projectPath) {
        AbstractExternalSystemSettings<?, ?, ?> settings = ExternalSystemApiUtil.getSettings(project, systemId);
        ExternalProjectSettings linkedProjectSettings = settings.getLinkedProjectSettings(projectPath);
        if (linkedProjectSettings == null) {
            return null;
        }
        String rootProjectPath = linkedProjectSettings.getExternalProjectPath();
        ExternalProjectInfo projectInfo = ProjectDataManager.getInstance().getExternalProjectData(project, systemId, rootProjectPath);
        if (projectInfo == null) {
            return null;
        }
        return projectInfo.getExternalProjectStructure();
    }

    /**
     * 2023.3 暂时性兼容
     *
     * @return
     */
    public static AccessToken resetThreadContext() {
        try {
            Class<?> threadContext = Class.forName("com.intellij.concurrency.ThreadContext");
            Method resetThreadContext = threadContext.getMethod("resetThreadContext");
            return (AccessToken) resetThreadContext.invoke(null);
        } catch (Exception ignore) {
            return AccessToken.EMPTY_ACCESS_TOKEN;
        }
    }
}
