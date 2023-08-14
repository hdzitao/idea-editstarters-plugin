package io.github.hdzitao.editstarters.ui.swing;

import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import io.github.hdzitao.editstarters.springboot.Starter;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.util.List;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

/**
 * StarterTable的抽象model
 *
 * @version 3.2.1
 */
public abstract class AbstractStarterTableModel extends AbstractTableModel {
    protected List<Starter> starters;
    protected final JBTable table;
    protected final int columnMax;

    protected final TableMouseClicker mouseClicker;

    public AbstractStarterTableModel(List<Starter> starters, JBTable table, int columnMax) {
        this.starters = starters;
        this.table = table;
        this.columnMax = columnMax;

        // 点击事件
        this.mouseClicker = new TableMouseClicker(table, columnMax);
        // 去掉标题/边框等等
        startersTableStyle(table);
        // model
        table.setModel(this);
        // 渲染
        render();
    }

    /**
     * 渲染
     */
    protected abstract void render();

    /**
     * 点击显示详情的column
     */
    protected abstract int getShowDescColumn();

    /**
     * 显示详情
     */
    public void setShowDescListener(ShowDescListener showDescListener) {
        mouseClicker.putListener(getShowDescColumn(), rowIndex -> {
            if (!inStarters(rowIndex)) {
                return;
            }

            Starter starter = starters.get(rowIndex);
            showDescListener.show(starter);
        });
    }

    @Override
    public int getRowCount() {
        return starters.size();
    }

    @Override
    public int getColumnCount() {
        return columnMax;
    }

    /**
     * 与starter相关的table样式
     * <p/>
     * 1. 删除表头
     * 2. 清除边框
     * 3. 被选择样式
     */
    protected void startersTableStyle(JBTable table) {
        table.setRowMargin(0);
        table.setBorder(JBUI.Borders.empty());
        table.setShowColumns(false);
        table.setShowGrid(false);
        table.setShowVerticalLines(false);
        table.setCellSelectionEnabled(false);
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(SINGLE_SELECTION);
    }

    /**
     * 设置TableColumn固定大小
     */
    protected void setFixWidth(TableColumn tableColumn, int width) {
        tableColumn.setResizable(false);
        tableColumn.setPreferredWidth(width);
        tableColumn.setMaxWidth(width);
        tableColumn.setMinWidth(width);
    }

    /**
     * 检查是否在starters
     */
    protected boolean inStarters(int row) {
        return row >= 0 && starters != null && row < starters.size();
    }
}
