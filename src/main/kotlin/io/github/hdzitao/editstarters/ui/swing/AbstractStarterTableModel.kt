package io.github.hdzitao.editstarters.ui.swing

import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import io.github.hdzitao.editstarters.springboot.Starter
import javax.swing.ListSelectionModel
import javax.swing.table.AbstractTableModel
import javax.swing.table.TableColumn

typealias ShowDescListener = (Starter) -> Unit

/**
 * StarterTable的抽象model
 *
 * @version 3.2.1
 */
abstract class AbstractStarterTableModel(
    protected var starters: MutableList<Starter>,
    protected val table: JBTable,
    protected val columnMax: Int
) : AbstractTableModel() {
    // 点击事件
    protected val mouseClicker: TableMouseClicker = TableMouseClicker(table, columnMax)

    // set/get value
    protected val tableValue: TableValue = TableValue(columnMax)

    /**
     * 准备好了
     */
    fun ready() {
        // 去掉标题/边框等等
        starterTableStyle(table)
        // 设置table值
        tableValue()
        // 渲染
        render()
        // model
        table.model = this
        // 自定义的准备
        readyOthers()
    }

    /**
     * 渲染
     */
    protected abstract fun render()

    /**
     * 设置tableValue
     */
    protected abstract fun tableValue()

    /**
     * 显示详情列
     */
    protected abstract val showDescColumn: Int

    /**
     * 准备其它的事
     */
    protected open fun readyOthers() {
    }

    /**
     * 显示详情
     */
    fun setShowDescListener(showDescProcessor: ShowDescListener) {
        mouseClicker.putListener(showDescColumn) { rowIndex: Int ->
            if (!inStarters(rowIndex)) {
                return@putListener
            }
            val starter = starters[rowIndex]
            showDescProcessor(starter)
        }
    }

    override fun getRowCount(): Int {
        return starters.size
    }

    override fun getColumnCount(): Int {
        return columnMax
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any? {
        return tableValue.getValueAt(rowIndex, columnIndex)
    }

    override fun setValueAt(value: Any, rowIndex: Int, columnIndex: Int) {
        tableValue.setValueAt(value, rowIndex, columnIndex)
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return tableValue.hasSetter(columnIndex)
    }

    /**
     * 与starter相关的table样式
     *
     *
     * 1. 删除表头
     * 2. 清除边框
     * 3. 被选择样式
     */
    protected fun starterTableStyle(table: JBTable) {
        table.rowMargin = 0
        table.border = JBUI.Borders.empty()
        table.setShowColumns(false)
        table.setShowGrid(false)
        table.showVerticalLines = false
        table.cellSelectionEnabled = false
        table.rowSelectionAllowed = true
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
    }

    /**
     * 设置TableColumn固定大小
     */
    protected fun setFixWidth(tableColumn: TableColumn, width: Int) {
        tableColumn.resizable = false
        tableColumn.preferredWidth = width
        tableColumn.maxWidth = width
        tableColumn.minWidth = width
    }

    /**
     * 检查是否在starters
     */
    protected fun inStarters(row: Int): Boolean {
        return row >= 0 && row < starters.size
    }

    /**
     * starter tableValue
     */
    protected fun starterTableValue(starterColumn: Int) {
        tableValue.putValuer(starterColumn, { row: Int, _: Int ->
            if (!inStarters(row)) {
                return@putValuer "Unknown"
            }
            starters[row]
        })
    }
}
