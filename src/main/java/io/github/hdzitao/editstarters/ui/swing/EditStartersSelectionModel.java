package io.github.hdzitao.editstarters.ui.swing;

import javax.swing.*;

/**
 * 顶层ListSelectionModel
 *
 * @version 3.2.0
 */
public class EditStartersSelectionModel extends DefaultListSelectionModel {

    @Override
    public void setSelectionInterval(int index0, int index1) {
        if (isSelectedIndex(index0)) {
            // 取消选中
            cancelCallback(index0, index1);

            removeSelectionInterval(index0, index1);
        } else {
            // 选中
            selectedCallback(index0, index1);

            addSelectionInterval(index0, index1);
        }
    }

    protected void selectedCallback(int index0, int index1) {
    }

    protected void cancelCallback(int index0, int index1) {
    }
}
