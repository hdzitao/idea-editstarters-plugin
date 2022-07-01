package hdzi.editstarters.ui;

import com.intellij.ui.CollectionListModel;
import hdzi.editstarters.dependency.StarterInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
public class StarterListCellRenderer extends JCheckBox implements ListCellRenderer<StarterInfo> {
    private CollectionListModel<StarterInfo> selectedListModel;
    @Getter
    private final Set<Integer> disableIndex = new HashSet<>();

    public StarterListCellRenderer(CollectionListModel<StarterInfo> selectedListModel) {
        this.selectedListModel = selectedListModel;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends StarterInfo> list, StarterInfo value, int index, boolean isSelected, boolean cellHasFocus) {
        this.setText(Objects.toString(value));

        if (this.selectedListModel != null && this.selectedListModel.contains(value)) {
            this.setSelected(false);
            this.setEnabled(false);

            this.disableIndex.add(index);
        } else {
            this.setSelected(isSelected);
            this.setEnabled(true);

            this.disableIndex.remove(index);
        }

        return this;
    }
}
