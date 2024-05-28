package io.github.hdzitao.editstarters.initializr

import io.github.hdzitao.editstarters.ohub.OHub
import io.github.hdzitao.editstarters.springboot.SpringBoot

/**
 * Initializr返回
 *
 * @version 3.2.0
 */
class InitializrResponse(
    var springBoot: SpringBoot? = null,
    var enableCache: Boolean = false,
    var cacheUpdateTime: Long = 0,
    var oHub: OHub? = null
)
