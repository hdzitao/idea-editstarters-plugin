package hdzi.editstarters.ui;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.psi.PsiFile;
import hdzi.editstarters.buildsystem.BuildSystem;
import hdzi.editstarters.dependency.SpringBoot;
import hdzi.editstarters.initializr.CachePersistentComponent;
import hdzi.editstarters.initializr.InitializrChain;
import hdzi.editstarters.initializr.InitializrParameters;
import hdzi.editstarters.initializr.InitializrStatus;
import hdzi.editstarters.version.Version;
import hdzi.editstarters.version.Versions;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Edit Starter按钮
 */
public abstract class EditStartersButtonAction extends AnAction {
    /**
     * 点击Edit Starter按钮动作
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        try {
            // 分析项目
            BuildSystem buildSystem = newBuildSystem(e.getDataContext());
            // 检查是否是spring boot
            if (!buildSystem.isSpringBootProject()) {
                throw new ShowErrorException("Not a Spring Boot Project!");
            }
            // spring boot version
            Version version = Versions.parse(buildSystem.getSpringbootDependency().getVersion());
            // 取缓存中的url作为弹出框的默认值
            String url = CachePersistentComponent.getInstance(e.getProject()).getUrl();
            // 弹出spring initializr地址输入框
            InitializrDialog initializrDialog = new InitializrDialog(url, version);
            initializrDialog.showDialog();
            // 取出新的url
            url = initializrDialog.getUrl();
            // 新的url为空,取消退出
            if (StringUtils.isBlank(url)) {
                return;
            }
            // 组装参数
            InitializrParameters parameters = new InitializrParameters();
            parameters.setProject(e.getProject());
            parameters.setBuildSystem(buildSystem);
            parameters.setUrl(url);
            parameters.setEnableCache(initializrDialog.isEnableCache());
            parameters.setVersion(version);
            parameters.setOthersHub(initializrDialog.getOthersHub());
            // 初始化状态
            InitializrStatus status = new InitializrStatus(parameters);
            // 执行
            ProgressManager progressManager = ProgressManager.getInstance();
            SpringBoot springBoot =
                    progressManager.runProcessWithProgressSynchronously((ThrowableComputable<SpringBoot, Exception>) () -> {
                        progressManager.getProgressIndicator().setIndeterminate(true);
                        return new InitializrChain().initialize(parameters, status);
                    }, "Loading " + url, true, e.getData(CommonDataKeys.PROJECT));
            new EditStartersDialog(buildSystem, springBoot, status).show();
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
    public void update(AnActionEvent e) {
        PsiFile data = e.getData(CommonDataKeys.PSI_FILE);
        if (data != null) {
            String name = data.getName();
            e.getPresentation().setEnabled(isMatchFile(name));
        }
    }

    /**
     * 判断文件名是否符合构建工具的要求
     */
    protected abstract boolean isMatchFile(String name);

    /**
     * new一个BuildSystem
     */
    protected abstract BuildSystem newBuildSystem(DataContext dataContext);
}
