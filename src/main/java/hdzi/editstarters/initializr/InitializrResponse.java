package hdzi.editstarters.initializr;

import java.util.Map;

public class InitializrResponse {
    Map<String, InitializrDependency> dependencies;
    Map<String, InitializrRepository> repositories;
    Map<String, InitializrBom> boms;
}