package hdzi.editstarters

import hdzi.editstarters.bean.StarterInfo

/**
 * Created by taojinhou on 2019/1/16.
 */
interface EditStarters {
    fun removeDependencies(dependencies: Collection<StarterInfo>)
    fun addDependencies(dependencies: Collection<StarterInfo>)
}