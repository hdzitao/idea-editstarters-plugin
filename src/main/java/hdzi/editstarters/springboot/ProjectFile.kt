package hdzi.editstarters.springboot

import hdzi.editstarters.springboot.bean.StarterInfo

/**
 * Created by taojinhou on 2019/1/16.
 */
interface ProjectFile {
    fun removeDependencies(dependencies: Collection<StarterInfo>)
    fun addDependencies(dependencies: Collection<StarterInfo>)
}