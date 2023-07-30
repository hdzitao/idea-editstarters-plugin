package io.github.hdzitao.editstarters.ui.swing;


import io.github.hdzitao.editstarters.dependency.Starter;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class EditStartersRenderer extends JCheckBox implements ListCellRenderer<Starter> {

    @Override
    public Component getListCellRendererComponent(JList<? extends Starter> list, Starter value, int index, boolean isSelected, boolean cellHasFocus) {
        this.setText(Objects.toString(value));
        this.setSelected(isSelected);

        return this;
    }
}
