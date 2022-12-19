package com.github.hdzitao.editstarters.ui;

import javax.swing.*;

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
