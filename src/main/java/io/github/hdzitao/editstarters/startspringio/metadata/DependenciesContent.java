package io.github.hdzitao.editstarters.startspringio.metadata;

import java.util.List;

/**
 * dependencies.content
 *
 * @version 3.2.0
 */
public class DependenciesContent {
    private String name;
    private List<MetadataDependency> content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MetadataDependency> getContent() {
        return content;
    }

    public void setContent(List<MetadataDependency> content) {
        this.content = content;
    }
}
