package hdzi.editstarters.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import hdzi.editstarters.gradle.GradleSpringBootEditor;
import hdzi.editstarters.ui.dialog.ExceptionAction;
import org.jetbrains.annotations.NotNull;

/**
 * Created by taojinhou on 2019/1/14.
 */
public class GradleButtonAction extends ExceptionAction {
    @Override
    public void invoke(@NotNull AnActionEvent e) {
        new GradleSpringBootEditor(e.getDataContext()).edit();
    }
}
