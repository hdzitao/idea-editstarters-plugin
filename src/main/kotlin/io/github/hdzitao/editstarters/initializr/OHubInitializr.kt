package io.github.hdzitao.editstarters.initializr

import com.google.gson.Gson
import com.intellij.util.io.HttpRequests
import io.github.hdzitao.editstarters.cache.InitializrCache
import io.github.hdzitao.editstarters.ohub.metadata.OHubBootVersion
import io.github.hdzitao.editstarters.springboot.SpringBootBuilder
import io.github.hdzitao.editstarters.startspringio.StartSpringIO
import io.github.hdzitao.editstarters.startspringio.StartSpringIO2SpringBoot
import io.github.hdzitao.editstarters.startspringio.metadata.MetadataConfig
import io.github.hdzitao.editstarters.ui.ShowErrorException

/**
 * 旧版本初始化
 *
 * @version 3.2.0
 */
class OHubInitializr : Initializr {
    private val gson = Gson()
    private val springIO2SpringBoot: SpringBootBuilder<StartSpringIO> = StartSpringIO2SpringBoot()

    override fun initialize(request: InitializrRequest, response: InitializrResponse, chain: InitializrChain) {
        val version = request.version
        val oHub = request.oHub

        if (oHub == null) {
            chain.initialize(request, response)
            return
        }

        response.oHub = oHub

        // 初始化旧版本配置
        val metadataMapUrl = oHub.metadataMapUrl
        val oHubBootVersion = HttpRequests.request(metadataMapUrl).connect { req ->
            gson.fromJson(req.readString(), OHubBootVersion::class.java)
        }
        val oHubMetaData = oHubBootVersion.match(version)
        if (oHubMetaData == null) {
            chain.initialize(request, response)
            return
        }
        // 获取旧版本metadata
        val metadataUrl = oHub.getMetadataUrl(
            oHubMetaData.metadataConfig ?: throw ShowErrorException("ohub‘s configure error!")
        )
        val metadata = HttpRequests.request(metadataUrl).connect { req ->
            gson.fromJson(req.readString(), MetadataConfig::class.java)
        }
        // 使用startspringio解析
        val startSpringIO = StartSpringIO(version, metadata)
        response.springBoot = springIO2SpringBoot.buildSpringBoot(startSpringIO)
        // 缓存
        if (request.enableCache) {
            val initializrCache = InitializrCache.getInstance(request.project)
            initializrCache.putOHubName(oHub.name)
        }
    }
}
