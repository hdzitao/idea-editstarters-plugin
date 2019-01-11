package hdzi.editstarters.ui.dialog

import com.intellij.openapi.ui.Messages


class ExceptionDialog(val e: Exception) {
    fun show() {
        Messages.showErrorDialog(
            """
                Please check the network connection and try again.
                If it fails again, please contact the author.
                Details:${e}
            """.trimIndent(),
            "Edit Starters Error"
        )
    }
}
