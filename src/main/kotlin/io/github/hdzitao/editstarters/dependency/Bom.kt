package io.github.hdzitao.editstarters.dependency

/**
 * bom
 *
 * @version 3.2.0
 */
open class Bom(
    var groupId: String? = null,
    var artifactId: String? = null,
    var version: String? = null,
) : Point {

    override fun point() = "$groupId:$artifactId"
}