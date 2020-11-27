package hdzi.editstarters.buildsystem

import com.intellij.psi.PsiElement
import hdzi.editstarters.springboot.Dependency

/**
 * Created by taojinhou on 2019/1/15.
 */
class ProjectDependency private constructor(
    override val groupId: String,
    override val artifactId: String,
    val version: String?,
    var element: PsiElement?
) : Dependency {
    constructor(groupId: String, artifactId: String) : this(groupId, artifactId, null, null)

    constructor(groupId: String, artifactId: String, version: String) : this(groupId, artifactId, version, null)

    constructor(groupId: String, artifactId: String, element: PsiElement) : this(groupId, artifactId, null, element)
}