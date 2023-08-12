package io.github.hdzitao.editstarters.ui.swing2;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.popup.IconButton;
import com.intellij.ui.InplaceButton;
import com.intellij.ui.table.JBTable;
import io.github.hdzitao.editstarters.springboot.Starter;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

/**
 * selected table model
 *
 * @version 3.2.0
 */
public class SelectedTableModel extends AbstractTableModel {
    private final JBTable selectedTable;
    private final List<Starter> selected;

    public SelectedTableModel(JBTable selectedTable, List<Starter> selected, Consumer<Starter> doRemove) {
        this.selectedTable = selectedTable;
        this.selected = selected;

        // 去掉标题/边框等等
//        selectedTable.setTableHeader(null);
        selectedTable.setRowMargin(0);
        selectedTable.setShowColumns(false);
        selectedTable.setShowGrid(false);
        selectedTable.setShowVerticalLines(false);
        selectedTable.setCellSelectionEnabled(false);
        selectedTable.setRowSelectionAllowed(false);
        selectedTable.setSelectionMode(SINGLE_SELECTION);

        // model
        selectedTable.setModel(this);

        // 渲染列
        TableColumnModel columnModel = selectedTable.getColumnModel();
        // 删除按钮列
        TableColumn removeBtnColumn = columnModel.getColumn(SelectedTableConstants.REMOVE_BUTTON_INDEX);
        // 设置大小
        removeBtnColumn.setResizable(false);
        removeBtnColumn.setPreferredWidth(SelectedTableConstants.REMOVE_BUTTON_WIDTH);
        removeBtnColumn.setMaxWidth(SelectedTableConstants.REMOVE_BUTTON_WIDTH);
        removeBtnColumn.setMinWidth(SelectedTableConstants.REMOVE_BUTTON_WIDTH);
        // 渲染按钮
        removeBtnColumn.setCellRenderer((table, value, isSelected, hasFocus, row, column) ->
                new InplaceButton(new IconButton("Delete", AllIcons.Actions.Close, AllIcons.Actions.CloseHovered), null));
        selectedTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    Point point = e.getPoint();
                    int column = selectedTable.columnAtPoint(point);
                    if (column == SelectedTableConstants.REMOVE_BUTTON_INDEX) {
                        int row = selectedTable.rowAtPoint(point);
                        if (row < selected.size()) {
                            doRemove.accept(selected.get(row));
                            selected.remove(row);
                            fireTableDataChanged();
                        }
                    }
                }
            }
        });
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
                if (rowIndex < selected.size()) {
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
