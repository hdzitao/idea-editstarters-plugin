package io.github.hdzitao.editstarters.initializr

import io.github.hdzitao.editstarters.cache.InitializrCache

/**
 * 缓存初始化
 *
 * @version 3.2.0
 */
class CacheInitializr : Initializr {
    override fun initialize(request: InitializrRequest, response: InitializrResponse, chain: InitializrChain) {
        val url = request.url
        val version = request.version.originalText
        val initializrCache = InitializrCache.getInstance(request.project)

        if (request.enableCache) {
            val springBoot = initializrCache.getSpringBoot(url, version)
            if (springBoot != null) {
                response.enableCache = true
                response.cacheUpdateTime = initializrCache.updateTime

                response.springBoot = springBoot
                return
            }
        }

        chain.initialize(request, response)

        if (request.enableCache && response.springBoot != null) {
            initializrCache.putSpringBoot(url, version, response.springBoot!!)
        }
    }
}