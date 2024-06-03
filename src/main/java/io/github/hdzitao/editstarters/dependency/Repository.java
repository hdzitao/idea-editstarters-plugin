package io.github.hdzitao.editstarters.dependency;


/**
 * 仓库
 *
 * @version 3.2.0
 */
public class Repository implements Point {
    protected String id;
    protected String name;
    protected String url;
    protected boolean snapshot = false;

    public Repository() {
    }

    public Repository(String id, String name, String url, boolean snapshot) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.snapshot = snapshot;
    }

    public Repository(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSnapshot() {
        return snapshot;
    }

    public void setSnapshot(boolean snapshot) {
        this.snapshot = snapshot;
    }

    @Override
    public String point() {
        return url;
    }
}