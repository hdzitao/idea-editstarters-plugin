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
            removeSelectionInterval(index0, index1);
        } else {
            addSelectionInterval(index0, index1);
        }
    }
}
