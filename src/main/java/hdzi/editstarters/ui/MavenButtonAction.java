package hdzi.editstarters.ui;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import hdzi.editstarters.maven.MavenSpringBootEditor;

/**
 * Created by taojinhou on 2019/1/11.
 */
public class MavenButtonAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        new MavenSpringBootEditor(e.getDataContext()).edit();
    }
}
