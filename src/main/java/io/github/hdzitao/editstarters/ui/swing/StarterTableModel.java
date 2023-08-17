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

    @Setter
    private StarterProcessor<Boolean> checkBoxValueProcessor;
    @Setter
    private StarterProcessor<Void> removeProcessor;
    @Setter
    private StarterProcessor<Void> addProcessor;

    public StarterTableModel(JBTable starterList) {
        super(Collections.emptyList(), starterList, COLUMN_MAX);
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
    protected void tableValue() {
        // starter
        starterTableValue(STARTER_INDEX);
        // 选项
        tableValue.putValuer(CHECKBOX_INDEX, (row, column) -> {
            if (!inStarters(row) || checkBoxValueProcessor == null) {
                return false;
            }

            return checkBoxValueProcessor.process(starters.get(row));
        }, (row, column, value) -> {
            boolean checked = Boolean.TRUE.equals(value);
            if (!inStarters(row)) {
                return;
            }

            Starter starter = starters.get(row);
            if (checked) {
                if (addProcessor != null) {
                    addProcessor.process(starter);
                }
            } else {
                if (removeProcessor != null) {
                    removeProcessor.process(starter);
                }
            }

            fireTableCellUpdated(row, column);
        });

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
}
