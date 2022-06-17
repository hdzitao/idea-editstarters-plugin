package hdzi.editstarters.buildsystem;

import hdzi.editstarters.dependency.Bom;
import lombok.Data;

@Data
public class ProjectBom implements Bom {
    private final String groupId;
    private final String artifactId;

    public ProjectBom(String groupId, String artifactId) {
        this.groupId = groupId;
        this.artifactId = artifactId;
    }
}