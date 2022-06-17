package hdzi.editstarters.initializr;

import hdzi.editstarters.dependency.Bom;
import lombok.Data;

import java.util.List;

@Data
public class InitializrBom implements Bom {
    private String groupId;
    private String artifactId;
    private String version;
    private List<String> repositories;
}