package io.github.hdzitao.editstarters.initializr;

/**
 * Initializr
 *
 * @version 3.2.0
 */
public interface Initializr {
    void initialize(InitializrRequest request, InitializrResponse response, InitializrChain chain) throws Exception;
}
