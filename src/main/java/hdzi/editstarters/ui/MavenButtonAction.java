package hdzi.editstarters.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import hdzi.editstarters.maven.MavenSpringBootEditor;
import hdzi.editstarters.ui.dialog.ExceptionAction;
import org.jetbrains.annotations.NotNull;

/**
 * Created by taojinhou on 2019/1/11.
 */
public class MavenButtonAction extends ExceptionAction {
    @Override
    public void invoke(@NotNull AnActionEvent e) {
        new MavenSpringBootEditor(e.getDataContext()).edit();
    }
}
