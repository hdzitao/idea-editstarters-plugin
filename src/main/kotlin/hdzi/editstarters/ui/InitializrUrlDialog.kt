package hdzi.editstarters.ui

import com.intellij.openapi.ui.InputValidator
import com.intellij.openapi.ui.Messages
import org.apache.commons.lang.StringUtils

class InitializrUrlDialog {
    private val urlVerifier = object : InputValidator {
        private val URL_PATTERN = "^https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".toRegex()

        override fun checkInput(inputString: String): Boolean = inputString.matches(this.URL_PATTERN)

        override fun canClose(inputString: String) = true
    }

    var url: String? = "https://start.spring.io/"
        private set

    val isOK: Boolean
        get() = StringUtils.isNotBlank(this.url)

    fun show(): InitializrUrlDialog {
        this.url = Messages.showInputDialog(
            null,
            "Spring Initializr Url",
            null,
            this.url,
            urlVerifier
        )

        return this
    }
}
