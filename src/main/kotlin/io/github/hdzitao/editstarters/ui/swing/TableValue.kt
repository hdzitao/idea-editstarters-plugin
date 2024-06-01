package io.github.hdzitao.editstarters.ui.swing

import com.jetbrains.rd.util.concurrentMapOf


private typealias Getter = (Int, Int) -> Any
private typealias Setter = (Int, Int, Any) -> Unit

/**
 * 通用table set/get value
 *
 * @version 3.2.1
 */
class TableValue(private val columnMax: Int) {
    private val setterMap: MutableMap<Int, Setter> = concurrentMapOf()
    private val getterMap: MutableMap<Int, Getter> = concurrentMapOf()

    /**
     * 设置getter/setter
     */
    fun putValuer(column: Int, getter: Getter?, setter: Setter? = null) {
        if (column in 0..<columnMax) {
            if (getter != null) {
                getterMap[column] = getter
            }

            if (setter != null) {
                setterMap[column] = setter
            }
        }
    }

    /**
     * 包含setter
     */
    fun hasSetter(column: Int): Boolean {
        return setterMap.containsKey(column)
    }

    /**
     * 获取值
     */
    fun getValueAt(row: Int, column: Int): Any? {
        val getter = getterMap[column] ?: return null

        return getter(row, column)
    }

    /**
     * 设置值
     */
    fun setValueAt(value: Any, row: Int, column: Int) {
        val setter = setterMap[column] ?: return

        setter(row, column, value)
    }
}
