package hdzi.editstarters.gradle

import hdzi.editstarters.bean.StarterInfo
import hdzi.editstarters.bean.initializr.InitializrBom

/**
 * Created by taojinhou on 2019/1/16.
 */

private val scopeConversion = mapOf(
    "compile" to "implementation",
    "test" to "testImplementation",
    "provided" to "compileOnly"
)

private fun mapScope(scope: String?): String =
    if (scope == null) {
        "implementation"
    } else {
        scopeConversion.getOrDefault(scope, scope)
    }

data class GradleSyntax(val instantiation: String, val point: String)

fun getDependencySyntax(info: StarterInfo): GradleSyntax {
    val instantiation = mapScope(info.scope)
    val starter = "${info.groupId}:${info.artifactId}"
    val version = if (info.version != null) ":${info.version}" else ""

    return GradleSyntax(instantiation, "$starter$version")
}

fun getBomSyntax(bom: InitializrBom): GradleSyntax {
    val instantiation = "mavenBom"
    val point = "${bom.groupId}:${bom.artifactId}"
    val version = if (bom.version != null) ":${bom.version}" else ""

    return GradleSyntax(instantiation, "$point$version")
}

fun splitGroupName(param: String): Pair<String, String> {
    val group = "^([^:]+):([^:]+)".toRegex().find(param)?.groupValues
    return Pair(group?.get(1) ?: "", group?.get(2) ?: "")
}

fun splitGroupName(namedArguments: Map<String?, String?>): Pair<String, String> =
    Pair(namedArguments["group"] ?: "", namedArguments["name"] ?: "")