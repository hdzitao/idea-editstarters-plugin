package io.github.hdzitao.editstarters.dependency;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 仓库
 *
 * @version 3.2.0
 */
@Getter
@Setter
@NoArgsConstructor
public class Repository implements Point {
    protected String id;
    protected String name;
    protected String url;
    protected boolean isSnapshot = false;

    public Repository(String id, String name, String url, boolean isSnapshot) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.isSnapshot = isSnapshot;
    }

    public Repository(String url) {
        this.url = url;
    }

    @Override
    public String point() {
        return url;
    }
}