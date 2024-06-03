package io.github.hdzitao.editstarters.dependency;

/**
 * 依赖
 *
 * @version 3.2.0
 */
public class Dependency implements Point {
    protected String groupId;
    protected String artifactId;
    protected String version;
    protected String scope;

    public Dependency() {
    }

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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String point() {
        return groupId + ":" + artifactId;
    }
}
