package hdzi.editstarters.buildsystem.gradle

import com.intellij.psi.PsiElement
import hdzi.editstarters.buildsystem.ProjectFile
import hdzi.editstarters.springboot.initializr.DependencyScope
import hdzi.editstarters.springboot.initializr.InitializrBom
import hdzi.editstarters.springboot.initializr.InitializrRepository
import hdzi.editstarters.springboot.initializr.StarterInfo

abstract class GradleSyntax<T : PsiElement> : ProjectFile<T>() {
    class Instruction(val instruction: String, val point: String) {
        operator fun component1() = instruction
        operator fun component2() = point
    }

    protected fun dependencyInstruction(info: StarterInfo): List<Instruction> {
        val instruction = info.scope.resolveScope()
        val version = if (info.version != null) ":${info.version}" else ""
        val point = "${info.groupId}:${info.artifactId}$version"
        val instructions = mutableListOf(Instruction(instruction, point))
        // 额外指令
        if (info.scope == DependencyScope.ANNOTATION_PROCESSOR) {
            instructions.add(Instruction("compileOnly", point))
        }
        return instructions
    }

    protected fun bomInstruction(bom: InitializrBom): Instruction {
        val point = "${bom.groupId}:${bom.artifactId}:${bom.version}"
        return Instruction("mavenBom", point)
    }

    protected fun repositoryInstruction(repository: InitializrRepository) =
        Instruction("maven", repository.url)

    protected fun splitGroupArtifact(point: String?): Pair<String, String> {
        val group = "^([^:]+):([^:]+)".toRegex().find(point ?: "")?.groupValues
        return Pair(group?.get(1) ?: "", group?.get(2) ?: "")
    }

    private fun DependencyScope.resolveScope() = when (this) {
        DependencyScope.ANNOTATION_PROCESSOR -> "annotationProcessor"
        DependencyScope.COMPILE -> "implementation"
        DependencyScope.COMPILE_ONLY -> "compileOnly"
        DependencyScope.PROVIDED -> "providedRuntime"
        DependencyScope.RUNTIME -> "runtimeOnly"
        DependencyScope.TEST -> "testImplementation"
    }
}