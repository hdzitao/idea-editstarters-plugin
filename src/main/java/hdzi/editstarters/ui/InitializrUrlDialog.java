package hdzi.editstarters.ui;

import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;

@Deprecated
class InitializrUrlDialog {
    private final InputValidator urlVerifier = new InputValidator() {
        @Override
        public boolean checkInput(String inputString) {
            return inputString.matches("^https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
        }

        @Override
        public boolean canClose(String inputString) {
            return true;
        }
    };

    public String getUrl() {
        return Messages.showInputDialog(
                null,
                "Spring Initializr Url",
                null,
                "https://start.spring.io/",
                urlVerifier
        );
    }
}
