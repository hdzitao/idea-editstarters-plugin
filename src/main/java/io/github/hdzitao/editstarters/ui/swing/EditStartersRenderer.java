package io.github.hdzitao.editstarters.ui.swing;


import io.github.hdzitao.editstarters.springboot.Starter;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * JList checkbox 顶层渲染器
 *
 * @version 3.2.0
 */
public class EditStartersRenderer extends JCheckBox implements ListCellRenderer<Starter> {

    @Override
    public Component getListCellRendererComponent(JList<? extends Starter> list, Starter value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(Objects.toString(value));
        setSelected(isSelected);

        return this;
    }
}
