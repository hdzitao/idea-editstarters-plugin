package io.github.hdzitao.editstarters.ui;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.psi.PsiFile;
import io.github.hdzitao.editstarters.buildsystem.BuildSystem;
import io.github.hdzitao.editstarters.cache.InitializrCache;
import io.github.hdzitao.editstarters.initializr.*;
import io.github.hdzitao.editstarters.ohub.GitHub;
import io.github.hdzitao.editstarters.ohub.Gitee;
import io.github.hdzitao.editstarters.ohub.OHub;
import io.github.hdzitao.editstarters.version.Version;
import io.github.hdzitao.editstarters.version.Versions;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Edit Starter按钮
 *
 * @version 3.2.0
 */
public abstract class EditStartersButtonAction extends AnAction {
    private final Initializr[] initializrs = {
            new CacheInitializr(),
            new StartSpringInitializr(),
            new OHubInitializr(),
    };

    private final OHub[] oHubs = {
            new GitHub(),
            new Gitee(),
    };

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
            if (project == null) {
                throw ShowErrorException.internal();
            }
            InitializrCache initializrCache = InitializrCache.getInstance(project);
            // 初始化
            initializrCache.initialize();
            // spring boot version
            Version version = Versions.parse(buildSystem.getSpringbootDependency().getVersion());
            // 弹出spring initializr地址输入框
            InitializrDialog initializrDialog = new InitializrDialog(initializrCache, version, oHubs);
            initializrDialog.showDialog();
            // 获取url
            String url = initializrDialog.getUrl();
            if (StringUtils.isBlank(url)) {
                return;
            }
            // 组装参数
            InitializrParameter parameter = new InitializrParameter()
                    .setProject(project)
                    .setBuildSystem(buildSystem)
                    .setVersion(version)
                    .setUrl(url)
                    .setEnableCache(initializrDialog.isEnableCache())
                    .setOHub(initializrDialog.getOHub());
            // 组装返回
            InitializrReturn ret = new InitializrReturn(parameter);
            // 执行
            ProgressManager progressManager = ProgressManager.getInstance();
            progressManager.runProcessWithProgressSynchronously((ThrowableComputable<Void, Exception>) () -> {
                progressManager.getProgressIndicator().setIndeterminate(true);
                new InitializrChain(initializrs).initialize(parameter, ret);
                return null;
            }, "Loading " + url, true, e.getData(CommonDataKeys.PROJECT));
            // 模块弹窗
            new EditStartersDialog(parameter, ret).show();
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

    /**
     * 判断文件名是否符合构建工具的要求
     */
    protected abstract boolean isMatched(String name);

    /**
     * new一个BuildSystem
     */
    protected abstract BuildSystem newBuildSystem(DataContext dataContext);
}
