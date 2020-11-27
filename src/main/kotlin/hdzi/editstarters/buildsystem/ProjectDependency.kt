package hdzi.editstarters.buildsystem

import com.intellij.psi.PsiElement
import hdzi.editstarters.springboot.Dependency

/**
 * Created by taojinhou on 2019/1/15.
 */
class ProjectDependency(
    override val groupId: String,
    override val artifactId: String,
    val version: String?
) : Dependency {
    constructor(groupId: String, artifactId: String) : this(groupId, artifactId, null)

    constructor(groupId: String, artifactId: String, element: PsiElement) : this(groupId, artifactId) {
        this.element = element
    }

    // 依赖的位置
    var element: PsiElement? = null

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