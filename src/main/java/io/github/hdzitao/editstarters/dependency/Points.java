package io.github.hdzitao.editstarters.dependency;

import java.util.Collection;
import java.util.Objects;

/**
 * Point方法集
 *
 * @version 3.2.0
 */
public final class Points {
    private Points() {
    }

    /**
     * 查找
     */
    public static <P extends Point> P find(Collection<P> points, Point point) {
        for (P p : points) {
            if (Objects.equals(point.point(), p.point())) {
                return p;
            }
        }

        return null;
    }

    /**
     * 包含
     */
    public static boolean contains(Collection<? extends Point> points, Point point) {
        return find(points, point) != null;
    }

    /**
     * 不重复添加
     */
    public static <P extends Point> void addUniq(Collection<P> points, P point) {
        if (point != null && !contains(points, point)) {
            points.add(point);
        }
    }

    /**
     * 不重复添加
     */
    public static <P extends Point> void addAllUniq(Collection<P> points, Collection<P> others) {
        for (P o : others) {
            addUniq(points, o);
        }
    }
}
