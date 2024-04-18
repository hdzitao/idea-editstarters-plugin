package io.github.hdzitao.editstarters.initializr

/**
 * Initializr
 *
 * @version 3.2.0
 */
interface Initializr {
    fun initialize(request: InitializrRequest, response: InitializrResponse, chain: InitializrChain)
}
