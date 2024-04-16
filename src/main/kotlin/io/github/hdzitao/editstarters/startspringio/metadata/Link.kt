package io.github.hdzitao.editstarters.startspringio.metadata

/**
 * 链接
 *
 * @version 3.2.0
 */
class Link(
    var rel: String? = null,
    var href: String? = null,
    var templated: Boolean = false,
    var title: String? = null,
    var description: String? = null
)