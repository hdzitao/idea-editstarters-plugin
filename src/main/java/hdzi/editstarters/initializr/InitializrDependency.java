package hdzi.editstarters.initializr;


import hdzi.editstarters.dependency.Dependency;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitializrDependency implements Dependency {
    private String groupId;
    private String artifactId;
    private String scope;
    private String version;
    private String repository;
    private String bom;
}