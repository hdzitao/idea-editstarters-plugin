package io.github.hdzitao.editstarters.dependency

/**
 * 依赖
 *
 * @version 3.2.0
 */
open class Dependency(
    var groupId: String? = null,
    var artifactId: String? = null,
    var version: String? = null,
    var scope: String? = null,
) : Point {

    override fun point() = "$groupId:$artifactId"
}
