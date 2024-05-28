package io.github.hdzitao.editstarters.initializr

import com.intellij.openapi.project.Project
import io.github.hdzitao.editstarters.buildsystem.BuildSystem
import io.github.hdzitao.editstarters.ohub.OHub
import io.github.hdzitao.editstarters.version.Version

/**
 * Initializr参数
 *
 * @version 3.2.0
 */
class InitializrRequest(
    val project: Project, // 项目
    val buildSystem: BuildSystem, // 构建系统
    val version: Version, // spring boot 版本
    val chain: InitializrChain, // 初始化链
    val supportedOHubs: List<OHub>, // 支持的ohub
) {
    var url: String = "https://start.spring.io/" // start.spring.io 地址
    var enableCache: Boolean = false // 是否启用缓存
    var oHub: OHub? = null // 旧版本处理信息
}