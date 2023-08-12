package io.github.hdzitao.editstarters.ui.swing2;

import io.github.hdzitao.editstarters.springboot.Starter;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * selected table model
 *
 * @version 3.2.0
 */
public class SelectedTableModel extends AbstractTableModel {

    private final List<Starter> selected;

    public SelectedTableModel(List<Starter> selected) {
        this.selected = selected;
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
                    return null;
                }
            case SelectedTableConstants.REMOVE_BUTTON_INDEX:
            default:
                return null;
        }
    }
}
