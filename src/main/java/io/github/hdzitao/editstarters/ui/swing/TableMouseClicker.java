package io.github.hdzitao.editstarters.ui.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通用table点击事件
 *
 * @version 3.2.0
 */
public class TableMouseClicker extends MouseAdapter {
    private final JTable table;
    private final Map<Integer, TableClickedListener> clickedListenerMap;

    public TableMouseClicker(JTable table, int columnMax) {
        this.table = table;
        this.clickedListenerMap = new ConcurrentHashMap<>(columnMax);

        table.addMouseListener(this);
    }

    public void put(Integer column, TableClickedListener clickedListener) {
        clickedListenerMap.put(column, clickedListener);
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) {
            return;
        }

        Point point = e.getPoint();
        int column = table.columnAtPoint(point);
        TableClickedListener clickedListener = clickedListenerMap.get(column);
        if (clickedListener == null) {
            return;
        }

        clickedListener.click(table.rowAtPoint(point));
    }
}
