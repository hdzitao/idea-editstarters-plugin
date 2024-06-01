package io.github.hdzitao.editstarters.ui.swing

import com.intellij.ui.BooleanTableCellEditor
import com.intellij.ui.BooleanTableCellRenderer
import com.intellij.ui.table.JBTable
import com.intellij.util.containers.ContainerUtil
import io.github.hdzitao.editstarters.springboot.Starter

private const val COLUMN_MAX = 2
private const val CHECKBOX_INDEX = 0
private const val STARTER_INDEX = 1
private const val CHECKBOX_WIDTH = 20

/**
 * starter列表 model
 *
 * @version 3.2.0
 */
class StarterTableModel(starterTable: JBTable) : AbstractStarterTableModel(mutableListOf(), starterTable, COLUMN_MAX) {
    var checkBoxValueProcessor: ((Starter) -> Boolean)? = null
    var removeProcessor: ((Starter) -> Unit)? = null
    var addProcessor: ((Starter) -> Unit)? = null

    override val showDescColumn: Int = STARTER_INDEX

    override fun render() {
        // 渲染列
        val columnModel = table.columnModel
        // 选择框
        val checkboxColumn = columnModel.getColumn(CHECKBOX_INDEX)
        // 设置大小
        setFixWidth(checkboxColumn, CHECKBOX_WIDTH)
        // 渲染
        checkboxColumn.cellRenderer = BooleanTableCellRenderer()
        checkboxColumn.cellEditor = BooleanTableCellEditor()
    }

    override fun tableValue() {
        // starter
        starterTableValue(showDescColumn)
        // 选项
        tableValue.putValuer(CHECKBOX_INDEX, { row: Int, _: Int ->
            inStarters(row) && checkBoxValueProcessor?.invoke(starters[row]) ?: false
        }, { row: Int, column: Int, value: Any ->
            if (!inStarters(row)) {
                return@putValuer
            }

            val starter = starters[row]
            if (value is Boolean && value) {
                addProcessor?.invoke(starter)
            } else {
                removeProcessor?.invoke(starter)
            }
            fireTableCellUpdated(row, column)
        })
    }

    /**
     * 重设starters
     */
    fun resetStarters(starters: List<Starter>?) {
        this.starters = ContainerUtil.notNullize(starters)
        fireTableDataChanged()
    }
}
