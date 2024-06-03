package io.github.hdzitao.editstarters.ui;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import io.github.hdzitao.editstarters.buildsystem.BuildSystem;
import io.github.hdzitao.editstarters.initializr.*;
import io.github.hdzitao.editstarters.ohub.GitHub;
import io.github.hdzitao.editstarters.ohub.Gitee;
import io.github.hdzitao.editstarters.ohub.OHub;
import io.github.hdzitao.editstarters.version.Version;
import io.github.hdzitao.editstarters.version.Versions;
import org.jetbrains.annotations.NotNull;

/**
 * Edit Starter按钮
 *
 * @version 3.2.0
 */
public abstract class EditStartersButtonAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        try {
            // 构建系统
            BuildSystem buildSystem = newBuildSystem(e.getDataContext());
            // 检查Spring boot
            if (!buildSystem.isSpringBootProject()) {
                throw new ShowErrorException("Not a Spring Boot Project!");
            }
            // 缓存
            Project project = e.getProject();
            // spring boot version
            Version version = Versions.parse(buildSystem.getSpringbootDependency().getVersion());
            // 组装参数
            InitializrRequest request = new InitializrRequest();
            request.setProject(project);
            request.setBuildSystem(buildSystem);
            request.setVersion(version);
            request.setSupportedOHubs(new OHub[]{
                    new GitHub(),
                    new Gitee(),
            });
            request.setChain(new InitializrChain(
                    new CacheInitializr(),
                    new StartSpringInitializr(),
                    new OHubInitializr()
            ));
            // 组装返回
            InitializrResponse response = new InitializrResponse();

            // 显示对话框
            InitializrDialog initializrDialog = new InitializrDialog(request, response);
            initializrDialog.show();
        } catch (Throwable throwable) { // 所有异常弹错误框
            String message;

            if (throwable instanceof ShowErrorException) {
                message = throwable.getMessage();
            } else {
                message = throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
            }

            Messages.showErrorDialog(message, "Edit Starters Error");
        }
    }

    /**
     * 判断按钮是否显示动作
     */
    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiFile data = e.getData(CommonDataKeys.PSI_FILE);
        if (data != null) {
            String name = data.getName();
            e.getPresentation().setEnabled(isMatched(name));
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    /**
     * 判断文件名是否符合构建工具的要求
     */
    protected abstract boolean isMatched(String name);

    /**
     * new一个BuildSystem
     */
    protected abstract BuildSystem newBuildSystem(DataContext dataContext);
}
