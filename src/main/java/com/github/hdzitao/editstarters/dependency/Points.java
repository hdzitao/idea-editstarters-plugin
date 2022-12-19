package com.github.hdzitao.editstarters.dependency;

import java.util.Collection;
import java.util.Objects;

public final class Points {
    private Points() {
    }

    public static <P extends Point> P find(Collection<P> main, Point p) {
        for (P m : main) {
            if (Objects.equals(p.point(), m.point())) {
                return m;
            }
        }

        return null;
    }

    public static boolean contains(Collection<? extends Point> main, Point p) {
        return find(main, p) != null;
    }

    public static <P extends Point> void addUniq(Collection<P> main, P p) {
        if (p != null && !contains(main, p)) {
            main.add(p);
        }
    }

    public static <P extends Point> void addAllUniq(Collection<P> main, Collection<P> others) {
        for (P o : others) {
            addUniq(main, o);
        }
    }
}
