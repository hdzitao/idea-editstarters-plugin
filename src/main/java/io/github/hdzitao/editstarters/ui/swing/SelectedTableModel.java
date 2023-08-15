package io.github.hdzitao.editstarters.ui.swing;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.popup.IconButton;
import com.intellij.ui.InplaceButton;
import com.intellij.ui.table.JBTable;
import io.github.hdzitao.editstarters.springboot.Starter;
import lombok.Setter;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.List;

/**
 * 已选择列表 model
 *
 * @version 3.2.0
 */
public class SelectedTableModel extends AbstractStarterTableModel {
    private static final int COLUMN_MAX = 2;
    private static final int REMOVE_BUTTON_INDEX = 0;
    private static final int STARTER_INDEX = 1;

    private static final int REMOVE_BUTTON_WIDTH = 20;

    @Setter
    private StarterProcessor<Void> removeProcessor;

    public SelectedTableModel(JBTable selectedTable, List<Starter> selected) {
        super(selected, selectedTable, COLUMN_MAX);
    }

    @Override
    protected void render() {
        // 渲染列
        TableColumnModel columnModel = table.getColumnModel();
        // 删除按钮列
        TableColumn removeBtnColumn = columnModel.getColumn(REMOVE_BUTTON_INDEX);
        // 大小
        setFixWidth(removeBtnColumn, REMOVE_BUTTON_WIDTH);
        // 渲染按钮(这个按钮无法接收点击事件)
        removeBtnColumn.setCellRenderer((table, value, isSelected, hasFocus, row, column) ->
                new InplaceButton(new IconButton("Delete", AllIcons.Actions.CloseHovered), null));
        // 点击事件
        mouseClicker.putListener(REMOVE_BUTTON_INDEX, rowIndex -> {
            if (inStarters(rowIndex) && removeProcessor != null) {
                removeProcessor.process(starters.get(rowIndex));
            }
        });
    }

    @Override
    protected void tableValue() {
        // starter
        starterTableValue(STARTER_INDEX);
    }

    @Override
    protected int getShowDescColumn() {
        return STARTER_INDEX;
    }

    /**
     * 添加
     */
    public void addStarter(Starter starter) {
        starters.add(starter);
        fireTableDataChanged();
    }

    /**
     * 删除
     */
    public void removeStarter(Starter starter) {
        starters.remove(starter);
        fireTableDataChanged();
    }

    /**
     * 删除
     */
    public void removeStarter(int row) {
        starters.remove(row);
        fireTableDataChanged();
    }

    /**
     * 包含
     */
    public boolean containsStarter(Starter starter) {
        return starters.contains(starter);
    }
}
