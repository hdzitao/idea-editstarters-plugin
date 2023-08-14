package io.github.hdzitao.editstarters.utils;

import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import io.github.hdzitao.editstarters.springboot.Starter;
import io.github.hdzitao.editstarters.ui.swing.ShowDescListener;
import io.github.hdzitao.editstarters.ui.swing.TableClickedListener;

import javax.swing.table.TableColumn;
import java.util.List;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

/**
 * ui工具类
 *
 * @version 3.2.1
 */
public final class UIUtils {
    private UIUtils() {
    }

    /**
     * 与starter相关的table样式
     * <p/>
     * 1. 删除表头
     * 2. 清除边框
     * 3. 被选择样式
     */
    public static void startersTableStyle(JBTable table) {
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
    public static void setFixWidth(TableColumn tableColumn, int width) {
        tableColumn.setResizable(false);
        tableColumn.setPreferredWidth(width);
        tableColumn.setMaxWidth(width);
        tableColumn.setMinWidth(width);
    }

    /**
     * showDescListener => TableClickedListener
     */
    public static TableClickedListener wrap(List<Starter> starters, ShowDescListener showDescListener) {
        return rowIndex -> {
            if (!CheckUtils.inRange(starters, rowIndex)) {
                return;
            }

            Starter starter = starters.get(rowIndex);
            showDescListener.show(starter);
        };
    }
}
