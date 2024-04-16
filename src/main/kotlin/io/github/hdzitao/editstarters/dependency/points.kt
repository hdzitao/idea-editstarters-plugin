package io.github.hdzitao.editstarters.dependency

/**
 * 查找
 */
fun Collection<Point>.find(point: Point) = this.find { it.point() == point.point() }

/**
 * 包含
 */
fun Collection<Point>.hasPoint(point: Point) = find(point) != null

/**
 * 不重复添加
 */
fun MutableCollection<Point>.addUniq(point: Point?) {
    if (point != null && !hasPoint(point)) {
        add(point)
    }
}

/**
 * 不重复添加
 */
fun MutableCollection<Point>.addAllUniq(others: Collection<Point>) {
    for (o in others) {
        addUniq(o)
    }
}