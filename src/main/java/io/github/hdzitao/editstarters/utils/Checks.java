package io.github.hdzitao.editstarters.utils;

import java.util.List;

/**
 * 检查类
 *
 * @version 3.2.0
 */
public final class Checks {
    private Checks() {
    }

    /**
     * 检查index是否在list的范围内
     *
     * @param list
     * @param index
     * @return
     */
    public static boolean inList(List<?> list, int index) {
        return index >= 0 && list != null && index < list.size();
    }
}
