package io.github.hdzitao.editstarters.dependency

/**
 * 依赖
 *
 * @version 3.2.0
 */
open class Dependency(
    val groupId: String,
    val artifactId: String,
    var version: String? = null,
    var scope: String? = null,
) : Point {

    override fun point() = "$groupId:$artifactId"
}
