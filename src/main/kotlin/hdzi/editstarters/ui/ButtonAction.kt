package hdzi.editstarters.ui.dialog

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import hdzi.editstarters.ui.ShowErrorException


abstract class ButtonAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        try {
            invoke(e)
        } catch (ex: Exception) {
            Messages.showErrorDialog(
                "Please check the network and try again. " +
                        "If it fails again, please contact the author. Details:\n" +
                        if (ex is ShowErrorException) ex.error else ex,
                "Edit Starters Error"
            )
        }
    }

    override fun update(e: AnActionEvent) {
        val name = e.getData(CommonDataKeys.PSI_FILE)?.name

        e.presentation.isEnabled = isMatchFile(name)
    }

    abstract fun invoke(e: AnActionEvent)

    abstract fun isMatchFile(name: String?): Boolean
}
