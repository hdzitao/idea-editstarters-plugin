package io.github.hdzitao.editstarters.dependency;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 依赖
 *
 * @version 3.2.0
 */
@Getter
@Setter
@NoArgsConstructor
public class Dependency implements Point {
    protected String groupId;
    protected String artifactId;
    protected String version;
    protected String scope;


    public Dependency(String groupId, String artifactId, String version, String scope) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.scope = scope;
        this.version = version;
    }

    public Dependency(String groupId, String artifactId, String version) {
        this(groupId, artifactId, version, null);
    }

    public Dependency(String groupId, String artifactId) {
        this(groupId, artifactId, null, null);
    }

    @Override
    public String point() {
        return this.groupId + ":" + this.artifactId;
    }
}
