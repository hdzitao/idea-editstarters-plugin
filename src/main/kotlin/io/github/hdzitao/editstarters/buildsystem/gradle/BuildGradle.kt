package io.github.hdzitao.editstarters.buildsystem.gradle

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.containers.ContainerUtil
import io.github.hdzitao.editstarters.buildsystem.DependencyElement
import io.github.hdzitao.editstarters.dependency.Bom
import io.github.hdzitao.editstarters.dependency.Dependency
import io.github.hdzitao.editstarters.dependency.Repository
import io.github.hdzitao.editstarters.springboot.Starter
import io.github.hdzitao.editstarters.ui.ShowErrorException
import org.apache.commons.lang3.ArrayUtils
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElement
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.statements.blocks.GrClosableBlock
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall

/**
 * build.gradle
 *
 * @version 3.2.0
 */
class BuildGradle(project: Project, override val buildFile: GroovyFile) :
    AbstractBuildGradle<GroovyFile, GrClosableBlock>() {

    private val factory = GroovyPsiElementFactory.getInstance(project)

    override fun GroovyFile.findOrCreateDependenciesTag(): GrClosableBlock {
        return getOrCreateClosure(TAG_DEPENDENCY_MANAGEMENT)
    }

    override fun GrClosableBlock.findAllDependencies(): List<Dependency> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, GrMethodCall::class.java)
            .map { call: GrMethodCall -> call.getDependencyGroupArtifact() }
            .toList()
    }


    override fun GrClosableBlock.createDependencyTag(starter: Starter) {
        val instructions = dependencyInstruction(starter)
        for (instruction in instructions) {
            val statement = factory.createStatementFromText(instruction.toInstString("\${inst} '\${point}'"))
            addStatementBefore(statement, null)
        }
    }

    override fun GroovyFile.findOrCreateBomsTag(): GrClosableBlock {
        return getOrCreateClosure(TAG_BOM_MANAGEMENT).getOrCreateClosure(TAG_BOM_IMPORT)
    }

    override fun GrClosableBlock.findAllBoms(): List<Bom> {
        return findAllMethod(TAG_BOM)
            .map { tag ->
                newByGroupArtifact(tag.getMethodFirstParam()) { groupId, artifactId ->
                    Bom(groupId, artifactId)
                }
            }
            .toList()
    }

    override fun GrClosableBlock.createBomTag(bom: Bom) {
        val instruction = bomInstruction(bom)
        val statement = factory.createStatementFromText(instruction.toInstString("\${inst} '\${point}'"))
        addStatementBefore(statement, null)
    }

    override fun GroovyFile.findOrCreateRepositoriesTag(): GrClosableBlock {
        return getOrCreateClosure(TAG_REPOSITORY_MANAGEMENT)
    }


    override fun GrClosableBlock.findAllRepositories(): List<Repository> {
        return findAllMethod(TAG_REPOSITORY)
            .map { tag ->
                val closureArguments = tag.closureArguments
                if (ArrayUtils.isEmpty(closureArguments)) {
                    return@map EMPTY
                }
                val urlCall = closureArguments[0].findMethod("url") ?: return@map EMPTY
                return@map urlCall.getMethodFirstParam()
            }
            .map { url -> Repository(url) }
            .toList()
    }

    override fun GrClosableBlock.createRepositoryTag(repository: Repository) {
        val instruction = repositoryInstruction(repository)
        val statement = factory.createStatementFromText(instruction.toInstString("\${inst} { url '\${point}' }"))
        addStatementBefore(statement, null)
    }

    /**
     * 闭包的获取或创建
     */
    private fun PsiElement.getOrCreateClosure(name: String): GrClosableBlock {
        var closure = findMethod(name)
        if (closure == null) {
            val statement = factory.createStatementFromText("$name {\n}")
            closure = if (this is GrClosableBlock) {
                addStatementBefore(statement, null) as GrMethodCall
            } else {
                add(statement) as GrMethodCall
            }
        }

        val closureArguments = closure.closureArguments
        // 新建不可能为空,为空即内部错误
        if (closureArguments.isNotEmpty()) {
            throw ShowErrorException.internal()
        }

        return closureArguments[0]
    }

    /**
     * 查找方法
     */
    private fun PsiElement.findMethod(name: String): GrMethodCall? {
        return ContainerUtil.find(PsiTreeUtil.getChildrenOfTypeAsList(this, GrMethodCall::class.java)) { call ->
            name == call.invokedExpression.text
        }
    }

    /**
     * 查找方法/批量
     */
    private fun PsiElement.findAllMethod(name: String): List<GrMethodCall> {
        val closableBlocks = PsiTreeUtil.getChildrenOfTypeAsList(this, GrMethodCall::class.java)
        return ContainerUtil.findAll(closableBlocks) { call -> name == call.invokedExpression.text }
    }

    /**
     * 获取方法第一个参数
     */
    private fun GrMethodCall.getMethodFirstParam(): String {
        val allArguments = argumentList.allArguments
        if (ArrayUtils.isEmpty(allArguments)) {
            return EMPTY
        }
        return allArguments[0].trimQuotation()
    }

    /**
     * 解析依赖语句
     */
    private fun GrMethodCall.getDependencyGroupArtifact(): DependencyElement<GrMethodCall> {
        val namedArguments = namedArguments.associate { argument ->
            argument.label.trimQuotation() to argument.expression.trimQuotation()
        }

        if (namedArguments.isEmpty()) {
            return newByGroupArtifact(getMethodFirstParam()) { groupId, artifactId ->
                DependencyElement(groupId, artifactId, this)
            }
        } else {
            val group = namedArguments["group"].checkEmpty()
            val name = namedArguments["name"].checkEmpty()
            return DependencyElement(group, name, this)
        }
    }

    /**
     * 删除头尾的引号
     */
    private fun GroovyPsiElement?.trimQuotation(): String {
        if (this == null) {
            return EMPTY
        }

        return text.trimQuotation()
    }

    /**
     * 删除头尾的引号
     */
    private fun String.trimQuotation(): String {
        return trimText('\'', '"')
    }
}
