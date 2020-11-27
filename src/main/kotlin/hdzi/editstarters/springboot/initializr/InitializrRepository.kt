package hdzi.editstarters.springboot.initializr

import hdzi.editstarters.springboot.Repository

class InitializrRepository(
    var id: String?,
    var name: String?,
    override var url: String?,
    var snapshotEnabled: Boolean?
) : Repository {
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