package io.github.hdzitao.editstarters.ui.swing

import com.jetbrains.rd.util.concurrentMapOf
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JTable

private typealias TableClickedListener = (Int) -> Unit

/**
 * 通用table点击事件
 *
 * @version 3.2.0
 */
class TableMouseClicker(private val table: JTable, private val columnMax: Int) : MouseAdapter() {
    private val clickedListenerMap: MutableMap<Int, TableClickedListener> = concurrentMapOf()

    init {
        table.addMouseListener(this)
    }

    /**
     * 设置点击事件
     */
    fun putListener(column: Int, clickedListener: TableClickedListener) {
        if (column in 0..<columnMax) {
            clickedListenerMap[column] = clickedListener
        }
    }


    override fun mouseClicked(e: MouseEvent) {
        if (e.button != MouseEvent.BUTTON1) {
            return
        }

        val point = e.point
        val column = table.columnAtPoint(point)
        val clickedListener = clickedListenerMap[column] ?: return

        clickedListener(table.rowAtPoint(point))
    }
}
