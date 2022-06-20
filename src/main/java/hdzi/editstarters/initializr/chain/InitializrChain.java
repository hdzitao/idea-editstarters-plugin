package hdzi.editstarters.initializr.chain;

import hdzi.editstarters.dependency.SpringBootProject;
import hdzi.editstarters.ui.ShowErrorException;

public class InitializrChain {
    private static final Initializr[] CHAIN = {
            new SpringInitializr(),
    };
    private int i = 0;

    public SpringBootProject initialize(InitializrParameters parameters) {
        if (i >= CHAIN.length) {
            throw new ShowErrorException("internal error!!!");
        }
        return CHAIN[i++].initialize(parameters, this);
    }
}
