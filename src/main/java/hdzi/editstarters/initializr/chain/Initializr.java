package hdzi.editstarters.initializr.chain;

import hdzi.editstarters.dependency.SpringBootProject;

public interface Initializr {
    SpringBootProject initialize(InitializrParameters parameters, InitializrChain chain);
}
