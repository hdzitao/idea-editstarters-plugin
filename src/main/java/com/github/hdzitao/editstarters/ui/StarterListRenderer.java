package com.github.hdzitao.editstarters.ui;

import com.github.hdzitao.editstarters.dependency.StarterInfo;
import com.intellij.ui.CollectionListModel;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class StarterListRenderer extends EditStartersRenderer {
    private final CollectionListModel<StarterInfo> selectedListModel;
    @Getter
    private final Set<Integer> disableIndex = new HashSet<>();

    public StarterListRenderer(CollectionListModel<StarterInfo> selectedListModel) {
        this.selectedListModel = selectedListModel;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends StarterInfo> list, StarterInfo value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

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
