package io.github.hdzitao.editstarters.initializr

import io.github.hdzitao.editstarters.ui.ShowErrorException

/**
 * Initializr处理链
 *
 * @version 3.2.0
 */
class InitializrChain(private val chain: Array<Initializr>) {
    private var i = 0

    fun initialize(request: InitializrRequest, response: InitializrResponse) {
        if (i >= chain.size) {
            throw ShowErrorException("Initialization Failed!")
        }

        chain[i++].initialize(request, response, this)
    }
}
