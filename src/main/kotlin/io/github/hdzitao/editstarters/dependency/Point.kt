package io.github.hdzitao.editstarters.dependency

/**
 * 依赖会多次转化,不使用equals和hashcode方法判断相同
 *
 * @version 3.2.0
 */
interface Point {
    fun point(): String
}

/**
 * 查找
 */
fun find(points: Collection<Point>, point: Point) = points.find { it.point() == point.point() }

/**
 * 包含
 */
fun contains(points: Collection<Point>, point: Point) = find(points, point) != null

/**
 * 不重复添加
 */
fun addUniq(points: MutableCollection<Point>, point: Point?) {
    if (point != null && !contains(points, point)) {
        points.add(point)
    }
}

/**
 * 不重复添加
 */
fun addAllUniq(points: MutableCollection<Point>, others: Collection<Point>) {
    for (o in others) {
        addUniq(points, o)
    }
}