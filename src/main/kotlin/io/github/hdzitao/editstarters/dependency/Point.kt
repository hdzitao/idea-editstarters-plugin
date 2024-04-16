package io.github.hdzitao.editstarters.dependency

/**
 * 依赖会多次转化,不使用equals和hashcode方法判断相同
 *
 * @version 3.2.0
 */
interface Point {
    fun point(): String
}