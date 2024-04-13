package io.github.hdzitao.editstarters

import io.github.hdzitao.editstarters.springboot.Starter


/**
 * 插件顶层接口
 *
 * @version 3.2.0
 */
interface EditStarters {
    /**
     * 添加starters
     */
    fun addStarters(dependencies: Collection<Starter>)

    /**
     * 删除starters
     */
    fun removeStarters(dependencies: Collection<Starter>)
}
