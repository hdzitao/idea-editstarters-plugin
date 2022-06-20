package hdzi.editstarters.initializr.chain;

import hdzi.editstarters.dependency.SpringBoot;

public interface Initializr {
    SpringBoot initialize(InitializrParameters parameters, InitializrChain chain);
}
