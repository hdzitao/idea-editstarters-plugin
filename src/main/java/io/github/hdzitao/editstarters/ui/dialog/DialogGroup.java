package io.github.hdzitao.editstarters.ui.dialog;

/**
 * ui组
 *
 * @version 4.0.0
 */
public class DialogGroup implements Dialog {
    private final Dialog[] dialogs;

    public DialogGroup(Dialog... dialogs) {
        this.dialogs = dialogs;
    }


    @Override
    public void show() {

    }
}
