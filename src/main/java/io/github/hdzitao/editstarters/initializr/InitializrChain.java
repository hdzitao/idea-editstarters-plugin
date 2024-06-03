package io.github.hdzitao.editstarters.initializr;

/**
 * Initializr处理链
 *
 * @version 3.2.0
 */
public class InitializrChain {
    private final Initializr[] chain;

    private int i = 0;

    public InitializrChain(Initializr... chain) {
        this.chain = chain;
    }

    public void initialize(InitializrRequest request, InitializrResponse response) throws Exception {
        chain[i++].initialize(request, response, this);
    }
}
