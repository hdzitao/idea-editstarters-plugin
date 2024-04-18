package io.github.hdzitao.editstarters.initializr

import com.google.gson.Gson
import com.intellij.util.io.HttpRequests
import io.github.hdzitao.editstarters.springboot.SpringBootBuilder
import io.github.hdzitao.editstarters.startspringio.StartSpringIO
import io.github.hdzitao.editstarters.startspringio.StartSpringIO2SpringBoot
import io.github.hdzitao.editstarters.startspringio.metadata.MetadataConfig

/**
 * 请求start.spring.io初始化
 *
 * @version 3.2.0
 */
class StartSpringInitializr : Initializr {
    private val gson = Gson()
    private val builder: SpringBootBuilder<StartSpringIO> = StartSpringIO2SpringBoot()

    override fun initialize(request: InitializrRequest, response: InitializrResponse, chain: InitializrChain) {
        val version = request.version
        val url: String = checkMetadataConfigLink(request.url)

        val metadata = HttpRequests.request(url).accept("application/json").connect { req ->
            gson.fromJson(req.readString(), MetadataConfig::class.java)
        }
        if (metadata == null || !metadata.match(version)) {
            chain.initialize(request, response)
            return
        }

        val startSpringIO = StartSpringIO(version, metadata)
        response.springBoot = builder.buildSpringBoot(startSpringIO)
    }

    private fun checkMetadataConfigLink(url: String): String {
        var myUrl = url
        if (myUrl.endsWith("/")) {
            myUrl = myUrl.substring(0, myUrl.length - 1)
        }

        val metadataLink = "/metadata/config"

        if (myUrl.endsWith(metadataLink)) {
            return myUrl
        }

        return myUrl + metadataLink
    }
}
