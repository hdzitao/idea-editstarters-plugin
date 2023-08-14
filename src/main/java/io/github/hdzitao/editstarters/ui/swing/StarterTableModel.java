package io.github.hdzitao.editstarters.ui.swing;

import com.intellij.ui.BooleanTableCellEditor;
import com.intellij.ui.BooleanTableCellRenderer;
import com.intellij.ui.table.JBTable;
import com.intellij.util.containers.ContainerUtil;
import io.github.hdzitao.editstarters.springboot.Starter;
import io.github.hdzitao.editstarters.utils.CheckUtils;
import io.github.hdzitao.editstarters.utils.UIUtils;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.Collections;
import java.util.List;


/**
 * starter列表 model
 *
 * @version 3.2.0
 */
public class StarterTableModel extends AbstractTableModel {
    private static final int COLUMN_MAX = 2;
    private static final int CHECKBOX_INDEX = 0;
    private static final int STARTER_INDEX = 1;

    private static final int CHECKBOX_WIDTH = 20;

    private List<Starter> starters;
    private final SelectedTableModel selectedTableModel;

    @Setter
    @Accessors(chain = true)
    private StarterRemoveListener removeListener;
    @Setter
    @Accessors(chain = true)
    private StarterAddListener addListener;

    private final TableMouseClicker mouseClicker;

    public StarterTableModel(JBTable starterList, SelectedTableModel selectedTableModel) {
        this.starters = Collections.emptyList();
        this.selectedTableModel = selectedTableModel;

        // 点击事件
        this.mouseClicker = new TableMouseClicker(starterList, COLUMN_MAX);

        // 去掉标题/边框等等
        UIUtils.startersTableStyle(starterList);

        // model
        starterList.setModel(this);

        // 渲染列
        TableColumnModel columnModel = starterList.getColumnModel();
        // 选择框
        TableColumn checkboxColumn = columnModel.getColumn(CHECKBOX_INDEX);
        // 设置大小
        UIUtils.setFixWidth(checkboxColumn, CHECKBOX_WIDTH);
        // 渲染
        checkboxColumn.setCellRenderer(new BooleanTableCellRenderer());
        checkboxColumn.setCellEditor(new BooleanTableCellEditor());
    }

    /**
     * 显示详情
     */
    public StarterTableModel setShowDescListener(ShowDescListener showDescListener) {
        mouseClicker.putListener(STARTER_INDEX, UIUtils.wrap(starters, showDescListener));

        return this;
    }

    /**
     * 刷新
     */
    public void refresh(List<Starter> starters) {
        this.starters = ContainerUtil.notNullize(starters);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return starters.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_MAX;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case STARTER_INDEX:
                if (CheckUtils.inRange(starters, rowIndex)) {
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
                if (!CheckUtils.inRange(starters, rowIndex)) {
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
