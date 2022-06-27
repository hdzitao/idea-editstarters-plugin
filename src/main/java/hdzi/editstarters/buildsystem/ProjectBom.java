package hdzi.editstarters.buildsystem;

import hdzi.editstarters.dependency.Bom;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectBom implements Bom {
    private final String groupId;
    private final String artifactId;
    private final String version;

    public ProjectBom(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public ProjectBom(String groupId, String artifactId) {
        this(groupId, artifactId, null);
    }
}