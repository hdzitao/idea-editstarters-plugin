package hdzi.editstarters.ui;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.ThrowableComputable;
import hdzi.editstarters.buildsystem.BuildSystem;
import hdzi.editstarters.initializr.SpringInitializr;

/**
 * Edit Starter按钮父类。
 */
public abstract class EditStartersButtonAction extends AnAction {
    /**
     * 点击Edit Starter按钮动作
     */
    @Override
    public void actionPerformed(AnActionEvent e) {
        try {
            BuildSystem buildSystem = newBuildSystem(e.getDataContext());
            if (buildSystem.isSpringBootProject()) {
                // 弹出spring initializr地址输入框
                String url = new InitializrUrlDialog().getUrl();
                ProgressManager progressManager = ProgressManager.getInstance();
                SpringInitializr springInitializr =
                        progressManager.runProcessWithProgressSynchronously((ThrowableComputable<SpringInitializr, Exception>) () -> {
                            progressManager.getProgressIndicator().setIndeterminate(true);
                            return new SpringInitializr(url, buildSystem.getSpringbootDependency().getVersion());
                        }, "Loading " + url, true, e.getData(CommonDataKeys.PROJECT));
                buildSystem.getExistsDependencyDB().values().forEach(springInitializr::addExistsStarter);
                new EditStartersDialog(buildSystem, springInitializr).show();
            } else {
                throw new ShowErrorException("Not a Spring Boot Project!");
            }
        } catch (Throwable throwable) { // 所有异常弹错误框
            String message;


            if (throwable instanceof ShowErrorException) {
                message = throwable.getMessage();
            } else {
                message = throwable.getClass().getName() + ": " + throwable.getMessage();
            }

            Messages.showErrorDialog(message, "Edit Starters Error");
        }
    }

    /**
     * 判断按钮是否显示动作
     */
    @Override
    public void update(AnActionEvent e) {
        String name = e.getData(CommonDataKeys.PSI_FILE).getName();
        e.getPresentation().setEnabled(isMatchFile(name));
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
