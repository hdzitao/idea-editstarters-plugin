package hdzi.editstarters.ui;

import lombok.NoArgsConstructor;

import javax.swing.*;

@NoArgsConstructor
public class StarterListSelectionModel extends DefaultListSelectionModel {
    private StarterListCellRenderer renderer;

    public StarterListSelectionModel(StarterListCellRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void setSelectionInterval(int index0, int index1) {
        if (this.renderer != null && this.renderer.getDisableIndex().contains(index0)) {
            return;
        }

        if (isSelectedIndex(index0)) {
            removeSelectionInterval(index0, index1);
        } else {
            addSelectionInterval(index0, index1);
        }
    }
}
