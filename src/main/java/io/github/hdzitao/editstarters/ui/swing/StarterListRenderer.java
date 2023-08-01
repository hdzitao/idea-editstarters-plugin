package io.github.hdzitao.editstarters.ui.swing;

import com.intellij.ui.CollectionListModel;
import io.github.hdzitao.editstarters.springboot.Starter;

import javax.swing.*;
import java.awt.*;

/**
 * starter list的渲染器
 *
 * @version 3.2.0
 */
public class StarterListRenderer extends EditStartersRenderer {
    private final JList<Starter> selectedList;

    public StarterListRenderer(JList<Starter> selectedList) {
        this.selectedList = selectedList;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Starter> list, Starter starter, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, starter, index, isSelected, cellHasFocus);

        CollectionListModel<Starter> selectedListModel = (CollectionListModel<Starter>) selectedList.getModel();
        setSelected(selectedListModel.contains(starter));

        return this;
    }
}
