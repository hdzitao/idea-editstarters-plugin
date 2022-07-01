package hdzi.editstarters.ui;

import hdzi.editstarters.dependency.StarterInfo;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class EditStartersRenderer extends JCheckBox implements ListCellRenderer<StarterInfo> {

    @Override
    public Component getListCellRendererComponent(JList<? extends StarterInfo> list, StarterInfo value, int index, boolean isSelected, boolean cellHasFocus) {
        this.setText(Objects.toString(value));
        this.setSelected(isSelected);

        return this;
    }
}
