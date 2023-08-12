package io.github.hdzitao.editstarters.ui.swing2;

import com.intellij.ui.BooleanTableCellEditor;
import com.intellij.ui.BooleanTableCellRenderer;
import com.intellij.ui.table.JBTable;
import io.github.hdzitao.editstarters.springboot.Starter;
import lombok.Getter;
import lombok.Setter;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.Collections;
import java.util.List;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

/**
 * starter table model
 *
 * @version 3.2.0
 */
public class StarterTableModel extends AbstractTableModel {
    private List<Starter> starters;
    private final SelectedTableModel selectedTableModel;

    @Getter
    @Setter
    private StarterRemoveListener removeListener;
    @Getter
    @Setter
    private StarterAddListener addListener;

    public StarterTableModel(JBTable starterList, SelectedTableModel selectedTableModel) {
        this.starters = Collections.emptyList();
        this.selectedTableModel = selectedTableModel;

        // 去掉标题/边框等等
//        starterList.setTableHeader(null);
        starterList.setRowMargin(0);
        starterList.setShowColumns(false);
        starterList.setShowGrid(false);
        starterList.setShowVerticalLines(false);
        starterList.setCellSelectionEnabled(false);
        starterList.setRowSelectionAllowed(true);
        starterList.setSelectionMode(SINGLE_SELECTION);

        // model
        starterList.setModel(this);

        // 渲染列
        TableColumnModel columnModel = starterList.getColumnModel();
        // 选择框
        TableColumn checkboxColumn = columnModel.getColumn(StarterTableConstants.CHECKBOX_INDEX);
        // 设置大小
        checkboxColumn.setResizable(false);
        checkboxColumn.setPreferredWidth(StarterTableConstants.CHECKBOX_WIDTH);
        checkboxColumn.setMaxWidth(StarterTableConstants.CHECKBOX_WIDTH);
        checkboxColumn.setMinWidth(StarterTableConstants.CHECKBOX_WIDTH);
        // 渲染
        checkboxColumn.setCellRenderer(new BooleanTableCellRenderer());
        checkboxColumn.setCellEditor(new BooleanTableCellEditor());
    }

    /**
     * 刷新
     *
     * @param starters
     */
    public void refresh(List<Starter> starters) {
        if (starters == null) {
            return;
        }

        this.starters = starters;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return starters.size();
    }

    @Override
    public int getColumnCount() {
        return StarterTableConstants.COLUMN_MAX;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case StarterTableConstants.STARTER_INDEX:
                if (rowIndex < starters.size()) {
                    return starters.get(rowIndex);
                } else {
                    return "Unknown";
                }
            case StarterTableConstants.CHECKBOX_INDEX:
                return selectedTableModel.containsStarter(starters.get(rowIndex));
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Boolean checked = (Boolean) aValue;
        if (checked && addListener != null) {
            addListener.do4add(starters.get(rowIndex));
        } else if (removeListener != null) {
            removeListener.do4remove(starters.get(rowIndex));
        }

        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == StarterTableConstants.CHECKBOX_INDEX;
    }
}
