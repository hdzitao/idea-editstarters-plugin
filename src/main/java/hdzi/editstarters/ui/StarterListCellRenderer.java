package hdzi.editstarters.ui;

import com.intellij.ui.CollectionListModel;
import hdzi.editstarters.dependency.StarterInfo;
import lombok.NoArgsConstructor;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

@NoArgsConstructor
public class StarterListCellRenderer extends JCheckBox implements ListCellRenderer<StarterInfo> {
    private CollectionListModel<StarterInfo> selectedListModel;

    public StarterListCellRenderer(CollectionListModel<StarterInfo> selectedListModel) {
        this.selectedListModel = selectedListModel;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends StarterInfo> list, StarterInfo value, int index, boolean isSelected, boolean cellHasFocus) {
        this.setText(Objects.toString(value));

        if (this.selectedListModel != null && this.selectedListModel.contains(value)) {
            this.setSelected(false);
            this.setEnabled(false);
        } else {
            this.setSelected(isSelected);
            this.setEnabled(true);
        }

        return this;
    }
}
