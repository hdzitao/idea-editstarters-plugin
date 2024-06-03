package io.github.hdzitao.editstarters.initializr;

import io.github.hdzitao.editstarters.ui.ShowErrorException;

/**
 * Initializr处理链
 *
 * @version 3.2.0
 */
public class InitializrChain {
    private final Initializr[] chain;

    private int i = 0;

    public InitializrChain(Initializr[] chain) {
        this.chain = chain;
    }

    public void initialize(InitializrRequest request, InitializrResponse response) throws Exception {
        if (i >= chain.length) {
            throw new ShowErrorException("Initialization Failed!");
        }

        chain[i++].initialize(request, response, this);
    }
}
