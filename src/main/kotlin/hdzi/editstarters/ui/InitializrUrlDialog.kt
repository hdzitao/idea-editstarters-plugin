package hdzi.editstarters.ui

import com.intellij.openapi.ui.InputValidator
import com.intellij.openapi.ui.Messages

class InitializrUrlDialog {
    private val urlVerifier = object : InputValidator {
        private val URL_PATTERN = "^https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".toRegex()

        override fun checkInput(inputString: String): Boolean = inputString.matches(this.URL_PATTERN)

        override fun canClose(inputString: String) = true
    }

    private val defaultUrl = "https://start.spring.io/"

    val url: String
        get() = Messages.showInputDialog(
            null,
            "Spring Initializr Url",
            null,
            defaultUrl,
            urlVerifier
        ) ?: defaultUrl
}
