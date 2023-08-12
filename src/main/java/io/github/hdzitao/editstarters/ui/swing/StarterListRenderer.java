package io.github.hdzitao.editstarters.ui.swing;

import io.github.hdzitao.editstarters.dependency.Points;
import io.github.hdzitao.editstarters.springboot.Starter;
import io.github.hdzitao.editstarters.ui.swing2.SelectedTableModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * starter list的渲染器
 *
 * @version 3.2.0
 */
public class StarterListRenderer extends EditStartersRenderer {
    private final SelectedTableModel selectedList;

    public StarterListRenderer(SelectedTableModel selectedList) {
        this.selectedList = selectedList;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Starter> list, Starter starter, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, starter, index, isSelected, cellHasFocus);

        List<Starter> selected = selectedList.getSelected();
        setSelected(Points.contains(selected, starter));

        return this;
    }
}
