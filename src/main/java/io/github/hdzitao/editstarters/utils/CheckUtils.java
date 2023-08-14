package io.github.hdzitao.editstarters.utils;

import java.util.List;

/**
 * 检查类
 *
 * @version 3.2.0
 */
public final class CheckUtils {
    private CheckUtils() {
    }

    /**
     * 检查index是否在list的范围内
     */
    public static boolean inRange(List<?> list, int index) {
        return index >= 0 && list != null && index < list.size();
    }
}
