package hdzi.editstarters.ui;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import hdzi.editstarters.gradle.GradleSpringBootEditor;
import hdzi.editstarters.ui.dialog.ExceptionDialog;

/**
 * Created by taojinhou on 2019/1/14.
 */
public class GradleButtonAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        try {
            new GradleSpringBootEditor(e.getDataContext()).edit();
        } catch (Exception ex) {
            new ExceptionDialog(ex).show();
        }
    }
}
