package hdzi.editstarters.ui.dialog

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.ThrowableComputable
import hdzi.editstarters.buildsystem.BuildSystem
import hdzi.editstarters.springboot.initializr.SpringInitializr
import hdzi.editstarters.ui.EditStartersDialog
import hdzi.editstarters.ui.InitializrUrlDialog
import hdzi.editstarters.ui.ShowErrorException

/**
 * Edit Starter按钮父类。
 */
abstract class EditStartersButtonAction : AnAction() {
    /**
     * 点击Edit Starter按钮动作
     */
    override fun actionPerformed(e: AnActionEvent) {
        try {
            val buildSystem = newBuildSystem(e.dataContext)
            if (buildSystem.isSpringBootProject) {
                // 弹出spring initializr地址输入框
                val url = InitializrUrlDialog().url
                val progressManager = ProgressManager.getInstance()
                val springInitializr =
                    progressManager.runProcessWithProgressSynchronously(ThrowableComputable<SpringInitializr, Exception> {
                        progressManager.progressIndicator.isIndeterminate = true
                        SpringInitializr(url, buildSystem.springbootDependency!!.version!!)
                    }, "Loading $url", true, e.getData(CommonDataKeys.PROJECT))
                buildSystem.existsDependencyDB.values.forEach { dep ->
                    springInitializr.addExistsStarter(dep)
                }
                EditStartersDialog(buildSystem, springInitializr).show()
            } else throw ShowErrorException("Not a Spring Boot Project!")
        } catch (throwable: Throwable) { // 所有异常弹错误框
            val message = if (throwable is ShowErrorException) {
                throwable.message
            } else {
                "${throwable.javaClass.name}: ${throwable.message}"
            }

            Messages.showErrorDialog(message, "Edit Starters Error")
        }
    }

    /**
     * 判断按钮是否显示动作
     */
    override fun update(e: AnActionEvent) {
        val name = e.getData(CommonDataKeys.PSI_FILE)?.name
        e.presentation.isEnabled = name?.isMatchFile() ?: false
    }

    /**
     * 判断文件名是否符合构建工具的要求
     */
    abstract fun String.isMatchFile(): Boolean

    /**
     * new一个BuildSystem
     */
    abstract fun newBuildSystem(dataContext: DataContext): BuildSystem
}
