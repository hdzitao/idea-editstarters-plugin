package hdzi.editstarters.springboot.initializr

import hdzi.editstarters.springboot.Dependency

/**
 * Created by taojinhou on 2018/12/21.
 */
class StarterInfo : Dependency {
    lateinit var id: String
    lateinit var name: String
    var description: String? = null
    var versionRange: String? = null

    // 坐标信息
    override lateinit var groupId: String
    override lateinit var artifactId: String
    var scope: String? = null
    var version: String? = null
    var repositories = mutableListOf<InitializrRepository>()
    var bom: InitializrBom? = null
    var exist = false

    val searchKey: String
        get() = "$groupId\t$artifactId\t$name\t$description".toLowerCase()

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
        return id.hashCode()
    }

    override fun toString(): String {
        return name
    }
}
