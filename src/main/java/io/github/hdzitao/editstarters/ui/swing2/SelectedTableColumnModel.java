package io.github.hdzitao.editstarters.ui.swing2;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.popup.IconButton;
import com.intellij.ui.InplaceButton;

import javax.swing.*;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionListener;
import java.util.Enumeration;

/**
 * selected table column model
 *
 * @version 3.2.0
 */
public class SelectedTableColumnModel implements TableColumnModel {
    private final TableColumnModel tableColumnModel;

    public SelectedTableColumnModel(TableColumnModel tableColumnModel, ActionListener removeButtonAction) {
        this.tableColumnModel = tableColumnModel;

        tableColumnModel.setColumnMargin(0);

        // 删除按钮
        TableColumn removeButtonColumn = tableColumnModel.getColumn(SelectedTableConstants.REMOVE_BUTTON_INDEX);
        removeButtonColumn.setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            InplaceButton deleteBtn = new InplaceButton(new IconButton("Delete", AllIcons.Actions.Close, AllIcons.Actions.CloseHovered),
                    removeButtonAction);
            deleteBtn.setBorder(null);
            return deleteBtn;
        });
        removeButtonColumn.setWidth(2);

    }

    @Override
    public void addColumn(TableColumn aColumn) {
        tableColumnModel.addColumn(aColumn);
    }

    @Override
    public void removeColumn(TableColumn column) {
        tableColumnModel.removeColumn(column);
    }

    @Override
    public void moveColumn(int columnIndex, int newIndex) {
        tableColumnModel.moveColumn(columnIndex, newIndex);
    }

    @Override
    public void setColumnMargin(int newMargin) {
        tableColumnModel.setColumnMargin(newMargin);
    }

    @Override
    public int getColumnCount() {
        return tableColumnModel.getColumnCount();
    }

    @Override
    public Enumeration<TableColumn> getColumns() {
        return tableColumnModel.getColumns();
    }

    @Override
    public int getColumnIndex(Object columnIdentifier) {
        return tableColumnModel.getColumnIndex(columnIdentifier);
    }

    @Override
    public TableColumn getColumn(int columnIndex) {
        return tableColumnModel.getColumn(columnIndex);
    }

    @Override
    public int getColumnMargin() {
        return tableColumnModel.getColumnMargin();
    }

    @Override
    public int getColumnIndexAtX(int xPosition) {
        return tableColumnModel.getColumnIndexAtX(xPosition);
    }

    @Override
    public int getTotalColumnWidth() {
        return tableColumnModel.getTotalColumnWidth();
    }

    @Override
    public void setColumnSelectionAllowed(boolean flag) {
        tableColumnModel.setColumnSelectionAllowed(flag);
    }

    @Override
    public boolean getColumnSelectionAllowed() {
        return tableColumnModel.getColumnSelectionAllowed();
    }

    @Override
    public int[] getSelectedColumns() {
        return tableColumnModel.getSelectedColumns();
    }

    @Override
    public int getSelectedColumnCount() {
        return tableColumnModel.getSelectedColumnCount();
    }

    @Override
    public void setSelectionModel(ListSelectionModel newModel) {
        tableColumnModel.setSelectionModel(newModel);
    }

    @Override
    public ListSelectionModel getSelectionModel() {
        return tableColumnModel.getSelectionModel();
    }

    @Override
    public void addColumnModelListener(TableColumnModelListener x) {
        tableColumnModel.addColumnModelListener(x);
    }

    @Override
    public void removeColumnModelListener(TableColumnModelListener x) {
        tableColumnModel.removeColumnModelListener(x);
    }
}
