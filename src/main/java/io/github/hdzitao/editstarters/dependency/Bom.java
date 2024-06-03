package io.github.hdzitao.editstarters.dependency;

/**
 * bom
 *
 * @version 3.2.0
 */
public class Bom implements Point {
    protected String groupId;
    protected String artifactId;
    protected String version;

    public Bom() {
    }

    public Bom(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public Bom(String groupId, String artifactId) {
        this(groupId, artifactId, null);
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

    @Override
    public String point() {
        return groupId + ":" + artifactId;
    }
}