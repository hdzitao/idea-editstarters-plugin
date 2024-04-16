package io.github.hdzitao.editstarters.springboot

import io.github.hdzitao.editstarters.dependency.Bom
import io.github.hdzitao.editstarters.dependency.Dependency
import io.github.hdzitao.editstarters.dependency.Repository
import io.github.hdzitao.editstarters.dependency.hasPoint

/**
 * spring boot starter
 *
 * @version 3.2.0
 */
class Starter : Dependency() {
    var id: String? = null
    var name: String? = null
    var description: String? = null
    var versionRange: String? = null

    val repositories: MutableList<Repository> = ArrayList()
    var bom: Bom? = null

    fun addRepository(id: String, repository: Repository?) {
        if (repository != null && !repositories.hasPoint(repository)) {
            repository.id = id
            repositories.add(repository)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Starter

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString() = name ?: "Unknown"

}