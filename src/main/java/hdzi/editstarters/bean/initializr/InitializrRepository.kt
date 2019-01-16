package hdzi.editstarters.bean.initializr

import hdzi.editstarters.bean.Repository

class InitializrRepository : Repository {
    var id: String? = null
    var name: String? = null
    override var url: String? = null
    var snapshotEnabled: Boolean? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InitializrRepository

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

}