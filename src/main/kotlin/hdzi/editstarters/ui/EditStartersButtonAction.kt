package hdzi.editstarters.ui.dialog

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
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
            doAction(e) // 执行动作
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
     * 执行Edit Starter按钮具体动作
     */
    abstract fun doAction(e: AnActionEvent)

    /**
     * 判断文件名是否符合构建工具的要求
     */
    abstract fun String.isMatchFile(): Boolean
}
