package io.github.hdzitao.editstarters.ui.swing;

import com.intellij.ui.CollectionListModel;
import io.github.hdzitao.editstarters.dependency.Dependency;
import io.github.hdzitao.editstarters.dependency.Points;
import io.github.hdzitao.editstarters.dependency.Starter;
import lombok.AllArgsConstructor;

import javax.swing.*;
import java.util.List;
import java.util.Set;

/**
 * starter list的ListSelectionModel
 *
 * @version 3.2.0
 */
@AllArgsConstructor
public class StarterListSelectionModel extends EditStartersSelectionModel {
    private final List<Dependency> existDependencies;

    private final JList<Starter> starterList;
    private final JList<Starter> selectedList;

    private final Set<Starter> addStarters;
    private final Set<Starter> removeStarters;

    @Override
    public void setSelectionInterval(int index0, int index1) {
        CollectionListModel<Starter> starterListModel = (CollectionListModel<Starter>) starterList.getModel();
        CollectionListModel<Starter> selectedListModel = (CollectionListModel<Starter>) selectedList.getModel();

        Starter starter = starterListModel.getElementAt(index0);

        if (isSelectedIndex(index0)) {
            // 取消选择
            removeSelectionInterval(index0, index1);

            if (Points.contains(existDependencies, starter)) {
                // 如果已存在,需要删除
                removeStarters.add(starter);
            } else {
                // 不存在,不添加
                addStarters.remove(starter);
            }

            selectedListModel.remove(starter);
        } else {
            // 添加
            addSelectionInterval(index0, index1);

            if (Points.contains(existDependencies, starter)) {
                // 已经存在,不删除
                removeStarters.remove(starter);
            } else {
                // 不存在,需要添加
                addStarters.add(starter);
            }

            if (!selectedListModel.contains(starter)) {
                selectedListModel.add(starter);
            }
        }
    }
}
