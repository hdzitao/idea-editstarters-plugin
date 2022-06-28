package hdzi.editstarters.initializr;

import hdzi.editstarters.dependency.IBom;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InitializrBom implements IBom {
    private String groupId;
    private String artifactId;
    private String version;
    private List<String> repositories;
}