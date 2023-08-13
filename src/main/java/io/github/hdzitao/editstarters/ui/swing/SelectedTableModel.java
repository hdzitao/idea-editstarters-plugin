package io.github.hdzitao.editstarters.ui.swing;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.popup.IconButton;
import com.intellij.ui.InplaceButton;
import com.intellij.ui.table.JBTable;
import io.github.hdzitao.editstarters.springboot.Starter;
import io.github.hdzitao.editstarters.utils.CheckUtils;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.List;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

/**
 * 已选择列表 model
 *
 * @version 3.2.0
 */
public class SelectedTableModel extends AbstractTableModel {
    private final List<Starter> selected;

    @Setter
    @Accessors(chain = true)
    private SelectedRemoveListener removeListener;

    private final TableMouseClicker mouseClicker;

    public SelectedTableModel(JBTable selectedTable, List<Starter> selected) {
        this.selected = selected;

        // 点击事件
        this.mouseClicker = new TableMouseClicker(selectedTable, SelectedTableConstants.COLUMN_MAX);

        // 去掉标题/边框等等
//        selectedTable.setTableHeader(null);
        selectedTable.setRowMargin(0);
        selectedTable.setShowColumns(false);
        selectedTable.setShowGrid(false);
        selectedTable.setShowVerticalLines(false);
        selectedTable.setCellSelectionEnabled(false);
        selectedTable.setRowSelectionAllowed(true);
        selectedTable.setSelectionMode(SINGLE_SELECTION);

        // model
        selectedTable.setModel(this);

        // 渲染列
        TableColumnModel columnModel = selectedTable.getColumnModel();
        // 删除按钮列
        TableColumn removeBtnColumn = columnModel.getColumn(SelectedTableConstants.REMOVE_BUTTON_INDEX);
        // 大小
        removeBtnColumn.setResizable(false);
        removeBtnColumn.setPreferredWidth(SelectedTableConstants.REMOVE_BUTTON_WIDTH);
        removeBtnColumn.setMaxWidth(SelectedTableConstants.REMOVE_BUTTON_WIDTH);
        removeBtnColumn.setMinWidth(SelectedTableConstants.REMOVE_BUTTON_WIDTH);
        // 渲染按钮(这个按钮无法接收点击事件)
        removeBtnColumn.setCellRenderer((table, value, isSelected, hasFocus, row, column) ->
                new InplaceButton(new IconButton("Delete", AllIcons.Actions.CloseHovered), null));
        // 点击事件
        mouseClicker.putListener(SelectedTableConstants.REMOVE_BUTTON_INDEX, rowIndex -> {
            if (CheckUtils.inRange(selected, rowIndex) && removeListener != null) {
                removeListener.remove(selected.get(rowIndex));
                removeStarter(rowIndex);
            }
        });
    }

    /**
     * 显示详情
     */
    public SelectedTableModel setShowDescListener(ShowDescListener showDescListener) {
        mouseClicker.putListener(SelectedTableConstants.STARTER_INDEX, rowIndex -> {
            if (!CheckUtils.inRange(selected, rowIndex)) {
                return;
            }

            Starter starter = selected.get(rowIndex);
            showDescListener.show(starter);
        });

        return this;
    }

    /**
     * 添加
     *
     * @param starter
     */
    public void addStarter(Starter starter) {
        selected.add(starter);
        fireTableDataChanged();
    }

    /**
     * 删除
     *
     * @param starter
     */
    public void removeStarter(Starter starter) {
        selected.remove(starter);
        fireTableDataChanged();
    }

    /**
     * 删除
     *
     * @param row
     */
    public void removeStarter(int row) {
        selected.remove(row);
        fireTableDataChanged();
    }

    /**
     * 包含
     *
     * @param starter
     * @return
     */
    public boolean containsStarter(Starter starter) {
        return selected.contains(starter);
    }

    @Override
    public int getRowCount() {
        return selected.size();
    }

    @Override
    public int getColumnCount() {
        return SelectedTableConstants.COLUMN_MAX;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case SelectedTableConstants.STARTER_INDEX:
                if (CheckUtils.inRange(selected, rowIndex)) {
                    return selected.get(rowIndex);
                } else {
                    return "Unknown";
                }
            case SelectedTableConstants.REMOVE_BUTTON_INDEX:
            default:
                return null;
        }
    }
}
