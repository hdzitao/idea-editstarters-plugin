package io.github.hdzitao.editstarters.ui.swing;

import com.intellij.ui.BooleanTableCellEditor;
import com.intellij.ui.BooleanTableCellRenderer;
import com.intellij.ui.table.JBTable;
import com.intellij.util.containers.ContainerUtil;
import io.github.hdzitao.editstarters.springboot.Starter;
import io.github.hdzitao.editstarters.utils.CheckUtils;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.Collections;
import java.util.List;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

/**
 * starter列表 model
 *
 * @version 3.2.0
 */
public class StarterTableModel extends AbstractTableModel {
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
        this.mouseClicker = new TableMouseClicker(starterList, StarterTableConstants.COLUMN_MAX);

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
     * 显示详情
     */
    public StarterTableModel setShowDescListener(ShowDescListener showDescListener) {
        mouseClicker.putListener(StarterTableConstants.STARTER_INDEX, rowIndex -> {
            if (!CheckUtils.inRange(starters, rowIndex)) {
                return;
            }

            Starter starter = starters.get(rowIndex);
            showDescListener.show(starter);
        });

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
        return StarterTableConstants.COLUMN_MAX;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case StarterTableConstants.STARTER_INDEX:
                if (CheckUtils.inRange(starters, rowIndex)) {
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
        switch (columnIndex) {
            case StarterTableConstants.CHECKBOX_INDEX:
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
            case StarterTableConstants.STARTER_INDEX:
            default:
                break;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == StarterTableConstants.CHECKBOX_INDEX;
    }
}
