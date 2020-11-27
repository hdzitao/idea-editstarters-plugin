package hdzi.editstarters

import hdzi.editstarters.springboot.initializr.StarterInfo

/**
 * Created by taojinhou on 2019/1/16.
 */
interface EditStarters {
    /**
     * 删除starters
     */
    fun removeStarters(dependencies: Collection<StarterInfo>)

    /**
     * 添加starters
     */
    fun addStarters(dependencies: Collection<StarterInfo>)
}