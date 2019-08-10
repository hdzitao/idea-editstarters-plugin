package hdzi.editstarters

import hdzi.editstarters.bean.StarterInfo

/**
 * Created by taojinhou on 2019/1/16.
 */
interface EditStarters {
    fun removeStarters(dependencies: Collection<StarterInfo>)
    fun addStarters(dependencies: Collection<StarterInfo>)
}