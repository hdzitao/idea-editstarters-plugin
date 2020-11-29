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

    companion object {
        // 依赖相关指令tag
        const val TAG_DEPENDENCY_MANAGEMENT = "dependencies"
        const val TAG_BOM_MANAGEMENT = "dependencyManagement"
        const val TAG_BOM_IMPORT = "imports"
        const val TAG_BOM = "mavenBom"
        const val TAG_REPOSITORY_MANAGEMENT = "repositories"
        const val TAG_REPOSITORY = "maven"

        // scope相关指令
        const val INS_ANNOTATION_PROCESSOR = "annotationProcessor"
        const val INS_COMPILE = "implementation"
        const val INS_COMPILE_ONLY = "compileOnly"
        const val INS_PROVIDED = "providedRuntime"
        const val INS_RUNTIME = "runtimeOnly"
        const val INS_TEST = "testImplementation"
    }

    protected fun dependencyInstruction(info: StarterInfo): List<Instruction> {
        val instruction = info.scope.resolveScope()
        val version = if (info.version != null) ":${info.version}" else ""
        val point = "${info.groupId}:${info.artifactId}$version"
        val instructions = mutableListOf(Instruction(instruction, point))
        // 额外指令
        if (info.scope == DependencyScope.ANNOTATION_PROCESSOR) {
            instructions.add(Instruction(INS_COMPILE_ONLY, point))
        }
        return instructions
    }

    protected fun bomInstruction(bom: InitializrBom): Instruction {
        val point = "${bom.groupId}:${bom.artifactId}:${bom.version}"
        return Instruction(TAG_BOM, point)
    }

    protected fun repositoryInstruction(repository: InitializrRepository) =
        Instruction(TAG_REPOSITORY, repository.url)

    protected fun splitGroupArtifact(point: String?): Pair<String, String> {
        val group = "^([^:]+):([^:]+)".toRegex().find(point ?: "")?.groupValues
        return Pair(group?.get(1) ?: "", group?.get(2) ?: "")
    }

    private fun DependencyScope.resolveScope() = when (this) {
        DependencyScope.ANNOTATION_PROCESSOR -> INS_ANNOTATION_PROCESSOR
        DependencyScope.COMPILE -> INS_COMPILE
        DependencyScope.COMPILE_ONLY -> INS_COMPILE_ONLY
        DependencyScope.PROVIDED -> INS_PROVIDED
        DependencyScope.RUNTIME -> INS_RUNTIME
        DependencyScope.TEST -> INS_TEST
    }
}