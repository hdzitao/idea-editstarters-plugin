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

    public static <P extends Point> P find(Collection<P> points, Point point) {
        for (P p : points) {
            if (Objects.equals(point.point(), p.point())) {
                return p;
            }
        }

        return null;
    }

    public static boolean contains(Collection<? extends Point> points, Point point) {
        return find(points, point) != null;
    }

}
