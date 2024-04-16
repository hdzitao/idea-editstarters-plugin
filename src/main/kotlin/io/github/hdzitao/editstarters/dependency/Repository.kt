package io.github.hdzitao.editstarters.dependency

/**
 * 仓库
 *
 * @version 3.2.0
 */
open class Repository(
    var url: String? = null,
    var id: String? = null,
    var name: String? = null,
    var isSnapshot: Boolean = false,
) : Point {

    override fun point() = url ?: ""
}