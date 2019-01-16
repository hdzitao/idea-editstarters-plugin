package hdzi.editstarters.bean.project

import hdzi.editstarters.bean.Dependency

/**
 * Created by taojinhou on 2019/1/15.
 */
class ProjectDependency(
    override val groupId: String,
    override val artifactId: String,
    val version: String?
) : Dependency {
    constructor(groupId: String, artifactId: String) : this(groupId, artifactId, null)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProjectDependency

        if (groupId != other.groupId) return false
        if (artifactId != other.artifactId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = groupId.hashCode()
        result = 31 * result + artifactId.hashCode()
        return result
    }
}