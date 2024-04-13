package io.github.hdzitao.editstarters.dependency

/**
 * bom
 *
 * @version 3.2.0
 */
open class Bom(
    val groupId: String,
    val artifactId: String,
    var version: String? = null,
) : Point {

    override fun point() = "$groupId:$artifactId"
}