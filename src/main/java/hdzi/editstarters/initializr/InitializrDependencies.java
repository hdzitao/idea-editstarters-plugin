package hdzi.editstarters.initializr;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class InitializrDependencies {
    private Map<String, InitializrDependency> dependencies;
    private Map<String, InitializrRepository> repositories;
    private Map<String, InitializrBom> boms;
}
