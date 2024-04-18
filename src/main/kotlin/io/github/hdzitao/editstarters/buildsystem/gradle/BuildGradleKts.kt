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
import org.apache.commons.lang3.StringUtils
import org.jetbrains.kotlin.psi.*
import java.util.*
import java.util.function.Function
import java.util.regex.Pattern

/**
 * build.gradle.kts
 * 参考 https://github.com/JetBrains/kotlin/blob/master/idea/idea-gradle/src/org/jetbrains/kotlin/idea/configuration/KotlinBuildScriptManipulator.kt
 *
 * @version 3.2.0
 */
internal class BuildGradleKts(project: Project, override val buildFile: KtFile) :
    AbstractBuildGradle<KtFile, KtBlockExpression>() {

    private val factory = KtPsiFactory(project)

    override fun KtFile.findOrCreateDependenciesTag(): KtBlockExpression {
        return TAG_DEPENDENCY_MANAGEMENT.getOrCreateTopBlock()
    }

    override fun KtBlockExpression.findAllDependencies(): List<Dependency> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, KtCallExpression::class.java)
            .map { ktCallExpression: KtCallExpression -> ktCallExpression.getDependencyGroupArtifact() }
            .toList()
    }

    override fun KtBlockExpression.createDependencyTag(starter: Starter) {
        val instructions: List<Instruction> = dependencyInstruction(starter)
        for (instruction in instructions) {
            addExpression(instruction.toInstString("\${inst}(\"\${point}\")"))
        }
    }

    override fun KtFile.findOrCreateBomsTag(): KtBlockExpression {
        return TAG_BOM_MANAGEMENT.getOrCreateTopBlock().getOrCreateBlock(TAG_BOM_IMPORT)
    }

    override fun KtBlockExpression.findAllBoms(): List<Bom> {
        return findAllCallExpression(TAG_BOM)
            .map { tag: KtCallExpression ->
                newByGroupArtifact(tag.getCallFirstParam()) { groupId, artifactId ->
                    Bom(groupId, artifactId)
                }
            }
            .toList()
    }

    override fun KtBlockExpression.createBomTag(bom: Bom) {
        val instruction: Instruction = bomInstruction(bom)
        addExpression(instruction.toInstString("\${inst}(\"\${point}\")"))
    }

    override fun KtFile.findOrCreateRepositoriesTag(): KtBlockExpression {
        return TAG_REPOSITORY_MANAGEMENT.getOrCreateTopBlock()
    }

    override fun KtBlockExpression.findAllRepositories(): List<Repository> {
        return findAllCallExpression(TAG_REPOSITORY).stream()
            .map { tag: KtCallExpression ->
                val arguments = tag.valueArguments
                val first = ContainerUtil.getFirstItem<KtValueArgument>(arguments) ?: return@map EMPTY
                if (first is KtLambdaArgument) {
                    val urlStatement = first.findCallLambdaStatement("url")
                    val right = urlStatement?.right ?: return@map EMPTY
                    if (right !is KtCallExpression) {
                        return@map EMPTY
                    }

                    return@map right.getCallFirstParam()
                } else {
                    return@map tag.getCallFirstParam()
                }
            }
            .map { url -> Repository(url) }
            .toList()
    }

    override fun KtBlockExpression.createRepositoryTag(repository: Repository) {
        val instruction: Instruction = repositoryInstruction(repository)
        addExpression(instruction.toInstString("\${inst} { url = uri(\"\${point}\") }"))
    }

    /**
     * 获取或创建顶层闭包
     */
    private fun String.getOrCreateTopBlock(): KtBlockExpression {
        val regex = callNameRegex()
        val statements = PsiTreeUtil.findChildrenOfAnyType(
            buildFile, KtScriptInitializer::class.java
        )
        val initializer = ContainerUtil.find(
            statements
        ) { it: KtScriptInitializer -> regex.matcher(it.text).find() }
        val expression = if (initializer == null) {
            buildFile.addExpression("$this {\n}") as KtCallExpression
        } else {
            PsiTreeUtil.findChildOfType(initializer, KtCallExpression::class.java)
        }

        return getLambdaBodyExpression(expression)
    }

    /**
     * 获取或创建闭包
     */
    private fun PsiElement.getOrCreateBlock(name: String): KtBlockExpression {
        var block = findCallExpression(name)
        if (block == null) {
            block = addExpression("$name {\n}") as KtCallExpression
        }

        return getLambdaBodyExpression(block)
    }

    /**
     * 查找语句
     */
    private fun PsiElement.findCallExpression(name: String): KtCallExpression? {
        val pattern = name.callNameRegex()
        val blocks = PsiTreeUtil.getChildrenOfTypeAsList(this, KtCallExpression::class.java)
        return ContainerUtil.find(blocks) { expression: KtCallExpression -> pattern.matcher(expression.text).find() }
    }

    /**
     * 查找语句/批量
     */
    private fun PsiElement.findAllCallExpression(name: String): List<KtCallExpression> {
        val pattern = name.callNameRegex()
        val blocks = PsiTreeUtil.getChildrenOfTypeAsList(this, KtCallExpression::class.java)
        return ContainerUtil.findAll(blocks) { expression: KtCallExpression -> pattern.matcher(expression.text).find() }
    }

    private fun String.callNameRegex(): Pattern {
        return Pattern.compile("^$this\\W")
    }

    /**
     * 第一参数
     */
    private fun KtCallExpression.getCallFirstParam(): String {
        val valueArguments = valueArguments
        if (ContainerUtil.isEmpty(valueArguments)) {
            return EMPTY
        }

        return valueArguments[0].trimQuotation()
    }

    /**
     * 获取依赖
     */
    private fun KtCallExpression.getDependencyGroupArtifact(): DependencyElement<KtCallExpression> {
        val namedArguments = valueArguments
            .filter { argument: KtValueArgument -> argument.getArgumentName() != null }
            .associate { argument ->
                argument.getArgumentName().trimQuotation() to argument.getArgumentExpression().trimQuotation()
            }

        if (namedArguments.isEmpty()) {
            return newByGroupArtifact(getCallFirstParam()) { groupId, artifactId ->
                DependencyElement<KtCallExpression>(groupId, artifactId, this)
            }
        } else {
            val group: String = namedArguments["group"].checkEmpty()
            val name: String = namedArguments["name"].checkEmpty()
            return DependencyElement(group, name, this)
        }
    }

    /**
     * 删除首尾引号
     */
    private fun String.trimQuotation(): String {
        return trimText('"')
    }

    /**
     * 删除首尾引号
     */
    private fun PsiElement?.trimQuotation(): String {
        if (this == null) {
            return EMPTY
        }

        return text.trimQuotation()
    }

    /**
     * 添加语句
     */
    private fun PsiElement.addExpression(text: String): PsiElement {
        val addEle = add(factory.createExpression(text))

        if (addEle.prevSibling != null
            && StringUtils.isNoneBlank(addEle.prevSibling.text)
        ) {
            addEle.parent.addBefore(factory.createNewLine(1), addEle)
        }

        if (addEle.nextSibling != null
            && StringUtils.isNoneBlank(addEle.nextSibling.text)
        ) {
            addEle.parent.addAfter(factory.createNewLine(1), addEle)
        }

        return addEle
    }

    private fun KtLambdaArgument.findCallLambdaStatement(leftName: String): KtBinaryExpression? {
        val statements = Optional.of<KtLambdaArgument>(this)
            .map<KtLambdaExpression>(Function<KtLambdaArgument, KtLambdaExpression> { getLambdaExpression() })
            .map<KtBlockExpression?> { obj: KtLambdaExpression -> obj.bodyExpression }
            .map<List<KtExpression>?> { obj: KtBlockExpression? -> obj!!.statements }
            .orElse(null)
        if (ContainerUtil.isEmpty(statements)) {
            return null
        }

        return ContainerUtil.find(statements) { statement: KtExpression? ->
            if (statement is KtBinaryExpression) {
                val left = statement.left
                if (left != null) {
                    return@find leftName == left.text
                }
            }
            false
        } as KtBinaryExpression?
    }

    companion object {
        /**
         * 获取lambda体
         */
        private fun getLambdaBodyExpression(block: KtCallExpression?): KtBlockExpression {
            // 创建不会为空
            if (block == null) {
                throw ShowErrorException.internal()
            }

            val lambdaArguments = block.lambdaArguments
            if (ContainerUtil.isEmpty(lambdaArguments)) {
                throw ShowErrorException.internal()
            }
            val ktLambdaArgument = lambdaArguments[0]
            val argumentExpression =
                ktLambdaArgument.getArgumentExpression()!! as? KtLambdaExpression ?: throw ShowErrorException.internal()
            val bodyExpression = argumentExpression.bodyExpression
                ?: throw ShowErrorException.internal()
            return bodyExpression
        }
    }
}