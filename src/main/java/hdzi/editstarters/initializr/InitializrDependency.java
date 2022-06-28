package hdzi.editstarters.initializr;


import hdzi.editstarters.dependency.Dependency;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitializrDependency extends Dependency {

    private String repository;
    private String bom;
}