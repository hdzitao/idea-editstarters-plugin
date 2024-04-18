package io.github.hdzitao.editstarters.dependency

/**
 * 查找
 */
fun <T : Point> Collection<T>.findPont(point: Point) = this.find { it.point() == point.point() }

/**
 * 包含
 */
fun Collection<Point>.hasPoint(point: Point) = findPont(point) != null

/**
 * 不重复添加
 */
fun <T : Point> MutableCollection<T>.addPointUniq(point: T?) {
    if (point != null && !hasPoint(point)) {
        add(point)
    }
}

/**
 * 不重复添加
 */
fun <T : Point> MutableCollection<T>.addAllPointsUniq(others: Collection<T>) {
    for (o in others) {
        addPointUniq(o)
    }
}