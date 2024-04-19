package io.github.hdzitao.editstarters.ui

/**
 * 自定义的错误信息
 *
 * @version 3.2.0
 */
class ShowErrorException : RuntimeException {
    constructor(message: String) : super(message)

    constructor(s: String, throwable: Throwable) : super(s, throwable)

    companion object {
        @JvmStatic
        fun internal(): ShowErrorException {
            return ShowErrorException("!!! internal error !!!")
        }
    }
}