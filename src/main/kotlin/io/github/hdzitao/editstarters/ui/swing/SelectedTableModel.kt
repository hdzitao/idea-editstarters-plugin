package io.github.hdzitao.editstarters.ui.swing

import com.intellij.icons.AllIcons
import com.intellij.openapi.ui.popup.IconButton
import com.intellij.ui.InplaceButton
import com.intellij.ui.table.JBTable
import io.github.hdzitao.editstarters.springboot.Starter
import javax.swing.table.TableCellRenderer


private const val COLUMN_MAX = 2
private const val REMOVE_BUTTON_INDEX = 0
private const val REMOVE_BUTTON_WIDTH = 20
private const val STARTER_INDEX = 1

/**
 * 已选择列表 model
 *
 * @version 3.2.0
 */

class SelectedTableModel(selectedTable: JBTable, selected: MutableList<Starter>) :
    AbstractStarterTableModel(selected, selectedTable, COLUMN_MAX) {

    override val showDescColumn: Int = STARTER_INDEX

    var removeProcessor: ((Starter) -> Unit)? = null

    override fun render() {
        // 渲染列
        val columnModel = table.columnModel
        // 删除按钮列
        val removeBtnColumn = columnModel.getColumn(REMOVE_BUTTON_INDEX)
        // 大小
        setFixWidth(removeBtnColumn, REMOVE_BUTTON_WIDTH)
        // 渲染按钮(这个按钮无法接收点击事件)
        removeBtnColumn.cellRenderer = TableCellRenderer { _, _, _, _, _, _ ->
            InplaceButton(IconButton("Delete", AllIcons.Actions.CloseHovered), null)
        }
    }

    override fun tableValue() {
        // starter
        starterTableValue(showDescColumn)
        // 点击事件
        mouseClicker.putListener(REMOVE_BUTTON_INDEX) { rowIndex: Int ->
            if (inStarters(rowIndex) && removeProcessor != null) {
                removeProcessor!!(starters[rowIndex])
            }
        }
    }

    /**
     * 添加
     */
    fun addStarter(starter: Starter) {
        starters.add(starter)
        fireTableDataChanged()
    }

    /**
     * 删除
     */
    fun removeStarter(starter: Starter) {
        starters.remove(starter)
        fireTableDataChanged()
    }

    /**
     * 删除
     */
    fun removeStarter(row: Int) {
        starters.removeAt(row)
        fireTableDataChanged()
    }

    /**
     * 包含
     */
    fun containsStarter(starter: Starter): Boolean {
        return starters.contains(starter)
    }
}
