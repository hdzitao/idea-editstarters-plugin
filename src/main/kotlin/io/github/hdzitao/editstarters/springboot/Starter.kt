package io.github.hdzitao.editstarters.springboot


import io.github.hdzitao.editstarters.dependency.Bom
import io.github.hdzitao.editstarters.dependency.Dependency
import io.github.hdzitao.editstarters.dependency.Repository
import io.github.hdzitao.editstarters.dependency.contains
import java.util.*

/**
 * spring boot starter
 *
 * @version 3.2.0
 */

class Starter : Dependency() {
    private val id: String? = null
    private val name: String? = null
    private val description: String? = null
    private val versionRange: String? = null

    private val repositories: MutableList<Repository> = ArrayList()
    private val bom: Bom? = null

    fun addRepository(id: String?, repository: Repository?) {
        if (repository != null && !contains(repositories, repository)) {
            repository.id = id
            repositories.add(repository)
        }
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as Starter
        return id == that.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    override fun toString(): String {
        return name!!
    }
}
