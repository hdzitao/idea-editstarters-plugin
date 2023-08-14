package io.github.hdzitao.editstarters.ui.swing;

import com.intellij.ui.BooleanTableCellEditor;
import com.intellij.ui.BooleanTableCellRenderer;
import com.intellij.ui.table.JBTable;
import com.intellij.util.containers.ContainerUtil;
import io.github.hdzitao.editstarters.springboot.Starter;
import lombok.Setter;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.Collections;
import java.util.List;


/**
 * starter列表 model
 *
 * @version 3.2.0
 */
public class StarterTableModel extends AbstractStarterTableModel {
    private static final int COLUMN_MAX = 2;
    private static final int CHECKBOX_INDEX = 0;
    private static final int STARTER_INDEX = 1;

    private static final int CHECKBOX_WIDTH = 20;

    private final SelectedTableModel selectedTableModel;

    @Setter
    private StarterRemoveListener removeListener;
    @Setter
    private StarterAddListener addListener;

    public StarterTableModel(JBTable starterList, SelectedTableModel selectedTableModel) {
        super(Collections.emptyList(), starterList, COLUMN_MAX);
        this.selectedTableModel = selectedTableModel;
    }

    @Override
    protected void render() {
        // 渲染列
        TableColumnModel columnModel = table.getColumnModel();
        // 选择框
        TableColumn checkboxColumn = columnModel.getColumn(CHECKBOX_INDEX);
        // 设置大小
        setFixWidth(checkboxColumn, CHECKBOX_WIDTH);
        // 渲染
        checkboxColumn.setCellRenderer(new BooleanTableCellRenderer());
        checkboxColumn.setCellEditor(new BooleanTableCellEditor());
    }

    @Override
    protected int getShowDescColumn() {
        return STARTER_INDEX;
    }

    /**
     * 刷新
     */
    public void refresh(List<Starter> starters) {
        this.starters = ContainerUtil.notNullize(starters);
        fireTableDataChanged();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case STARTER_INDEX:
                if (inStarters(rowIndex)) {
                    return starters.get(rowIndex);
                } else {
                    return "Unknown";
                }
            case CHECKBOX_INDEX:
                return selectedTableModel.containsStarter(starters.get(rowIndex));
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case CHECKBOX_INDEX:
                Boolean checked = (Boolean) aValue;
                if (!inStarters(rowIndex)) {
                    break;
                }

                Starter starter = starters.get(rowIndex);
                if (checked && addListener != null) {
                    addListener.add(starter);
                } else if (removeListener != null) {
                    removeListener.remove(starter);
                }

                fireTableCellUpdated(rowIndex, columnIndex);

                break;
            case STARTER_INDEX:
            default:
                break;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == CHECKBOX_INDEX;
    }
}
