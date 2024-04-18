package io.github.hdzitao.editstarters.buildsystem.gradle

import com.intellij.psi.PsiElement
import io.github.hdzitao.editstarters.buildsystem.DependencyScope
import io.github.hdzitao.editstarters.buildsystem.ProjectFile
import io.github.hdzitao.editstarters.dependency.Bom
import io.github.hdzitao.editstarters.dependency.Repository
import io.github.hdzitao.editstarters.springboot.Starter
import org.apache.commons.lang3.ArrayUtils
import org.apache.commons.lang3.StringUtils

/**
 * build.gradle抽象类
 *
 * @version 3.2.0
 */
abstract class AbstractBuildGradle<T : PsiElement> : ProjectFile<T>() {
    /**
     * gradle语法简单抽象
     */
    class Instruction(val inst: String, val point: String) {
        fun toInstString(format: String): String {
            return format.replace("\${inst}", inst).replace("\${point}", point)
        }
    }

    companion object {
        // TAG ==============================================================================
        const val TAG_DEPENDENCY_MANAGEMENT: String = "dependencies"
        const val TAG_BOM_MANAGEMENT: String = "dependencyManagement"
        const val TAG_BOM_IMPORT: String = "imports"
        const val TAG_BOM: String = "mavenBom"
        const val TAG_REPOSITORY_MANAGEMENT: String = "repositories"
        const val TAG_REPOSITORY: String = "maven"
        // TAG ==============================================================================
    }

    /**
     * dependency语法
     */
    protected fun dependencyInstruction(info: Starter): List<Instruction> {
        val instructions = ArrayList<Instruction>()
        val point = splicingDependency(info.groupId, info.artifactId, info.version)
        for (inst in DependencyScope.getByScope(info.scope).resolveGradleScope()) {
            instructions.add(Instruction(inst, point))
        }
        return instructions
    }

    /**
     * bom语法
     */
    protected fun bomInstruction(bom: Bom): Instruction {
        val point = splicingDependency(bom.groupId, bom.artifactId, bom.version)
        return Instruction(TAG_BOM, point)
    }

    /**
     * repository语句
     */
    protected fun repositoryInstruction(repository: Repository): Instruction {
        return Instruction(TAG_REPOSITORY, repository.url ?: EMPTY)
    }

    /**
     * 割出GroupID/ArtifactID构建Depend
     */
    protected fun <Depend> newByGroupArtifact(point: String, buildFun: (String, String) -> Depend): Depend {
        if (StringUtils.isNoneBlank(point)) {
            val groupArtifact = point.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (groupArtifact.size >= 2) {
                return buildFun(groupArtifact[0], groupArtifact[1])
            }
        }
        return buildFun(EMPTY, EMPTY)
    }

    /**
     * 处理scope
     */
    protected fun DependencyScope.resolveGradleScope(): Array<String> {
        return when (this) {
            DependencyScope.ANNOTATION_PROCESSOR -> arrayOf("compileOnly", "annotationProcessor")
            DependencyScope.COMPILE -> arrayOf("implementation")
            DependencyScope.COMPILE_ONLY -> arrayOf("compileOnly")
            DependencyScope.PROVIDED -> arrayOf("providedRuntime")
            DependencyScope.RUNTIME -> arrayOf("runtimeOnly")
            DependencyScope.TEST -> arrayOf("testImplementation")
        }
    }

    /**
     * " aaa " => "aaa"
     */
    protected fun trimText(s: String?, vararg chars: Char): String {
        if (s == null) {
            return EMPTY
        }

        var len = s.length
        var st = 0
        val `val` = s.toCharArray()

        while ((st < len) && (ArrayUtils.contains(chars, `val`[st]))) {
            st++
        }
        while ((st < len) && (ArrayUtils.contains(chars, `val`[len - 1]))) {
            len--
        }
        return if (((st > 0) || (len < s.length))) s.substring(st, len) else s
    }

    /**
     * 检查空
     */
    protected fun checkEmpty(s: String?): String {
        return s ?: EMPTY
    }

    /**
     * 拼接 groupId:artifactId:version
     */
    protected fun splicingDependency(groupId: String?, artifactId: String?, version: String?): String {
        val groupId = checkEmpty(groupId)
        val artifactId = checkEmpty(artifactId)

        var point = "$groupId:$artifactId"

        if (StringUtils.isNoneBlank(version)) {
            point = "$point:$version"
        }

        return point
    }
}