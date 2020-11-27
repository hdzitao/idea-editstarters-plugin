package hdzi.editstarters.buildsystem.gradle

import hdzi.editstarters.springboot.initializr.InitializrBom
import hdzi.editstarters.springboot.initializr.InitializrRepository
import hdzi.editstarters.springboot.initializr.StarterInfo

interface GradleSyntax {
    fun dependencyInstruction(info: StarterInfo): GradleInstruction {
        val instruction = mapScope(info.scope)
        val starter = "${info.groupId}:${info.artifactId}"
        val version = if (info.version != null) ":${info.version}" else ""

        return GradleInstruction(instruction, "$starter$version")
    }

    fun bomInstruction(bom: InitializrBom): GradleInstruction {
        val instruction = "mavenBom"
        val point = "${bom.groupId}:${bom.artifactId}"
        val version = if (bom.version != null) ":${bom.version}" else ""

        return GradleInstruction(instruction, "$point$version")
    }

    fun repositoryInstruction(repository: InitializrRepository) =
        GradleInstruction("maven", "{ url '${repository.url}' }")

    fun splitGroupName(param: String): Pair<String, String> {
        val group = "^([^:]+):([^:]+)".toRegex().find(param)?.groupValues
        return Pair(group?.get(1) ?: "", group?.get(2) ?: "")
    }

    fun splitGroupName(namedArguments: Map<String?, String?>) =
        Pair(namedArguments["group"] ?: "", namedArguments["name"] ?: "")

    companion object {
        val scopeConversion = mapOf(
            "compile" to "implementation",
            "test" to "testImplementation",
            "provided" to "compileOnly"
        )

        fun mapScope(scope: String?): String =
            if (scope == null) {
                "implementation"
            } else {
                scopeConversion.getOrDefault(scope, scope)
            }
    }
}