package hdzi.editstarters.bean

import hdzi.editstarters.bean.initializr.InitializrBom
import hdzi.editstarters.bean.initializr.InitializrRepository

/**
 * Created by taojinhou on 2018/12/21.
 */
class StarterInfo : Dependency {
    // 基本信息
    var id: String? = null
    var name: String? = null
    var description: String? = null
    var versionRange: String? = null
    // 坐标信息
    override var groupId: String? = null
    override var artifactId: String? = null
    var scope: String? = null
    var version: String? = null
    var repositories = mutableSetOf<InitializrRepository>()
    var bom: InitializrBom? = null
    var exist = false

    val searchKey: String
        get() = "$groupId\t$artifactId\t$name\t$description".toLowerCase()

    val canBeAdded: Boolean
        get() = groupId != null && artifactId != null

    fun addRepository(repository: InitializrRepository?) {
        if (repository != null) {
            this.repositories.add(repository)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StarterInfo

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return name!!
    }
}
