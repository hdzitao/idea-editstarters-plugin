package hdzi.editstarters.initializr;


import hdzi.editstarters.dependency.IDependency;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitializrDependency implements IDependency {
    private String groupId;
    private String artifactId;
    private String scope;
    private String version;
    private String repository;
    private String bom;
}