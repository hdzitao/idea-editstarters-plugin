package io.github.hdzitao.editstarters.ui.swing;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通用table set/get value
 *
 * @version 3.2.1
 */
public class TableValue {
    private final int columnMax;
    private final Map<Integer, Setter> setterMap;
    private final Map<Integer, Getter> getterMap;

    /**
     * setter
     */
    public static interface Setter {
        void set(int row, int column, Object value);
    }

    /**
     * getter
     */
    public static interface Getter {
        Object get(int row, int column);
    }

    public TableValue(int columnMax) {
        this.columnMax = columnMax;

        this.setterMap = new ConcurrentHashMap<>(columnMax);
        this.getterMap = new ConcurrentHashMap<>(columnMax);
    }

    /**
     * 设置getter/setter
     */
    public void putValuer(int column, Getter getter) {
        putValuer(column, getter, null);
    }

    /**
     * 设置getter/setter
     */
    public void putValuer(int column, Getter getter, Setter setter) {
        if (column >= 0 && column < columnMax) {
            if (getter != null) {
                getterMap.put(column, getter);
            }

            if (setter != null) {
                setterMap.put(column, setter);
            }
        }
    }

    /**
     * 包含setter
     */
    public boolean hasSetter(int column) {
        return setterMap.containsKey(column);
    }

    /**
     * 获取值
     */
    public Object getValueAt(int row, int column) {
        Getter getter = getterMap.get(column);
        if (getter == null) {
            return null;
        }

        return getter.get(row, column);
    }

    /**
     * 设置值
     */
    public void setValueAt(Object value, int row, int column) {
        Setter setter = setterMap.get(column);
        if (setter == null) {
            return;
        }

        setter.set(row, column, value);
    }
}
