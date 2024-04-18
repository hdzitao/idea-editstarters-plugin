package io.github.hdzitao.editstarters.buildsystem.gradle

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import io.github.hdzitao.editstarters.buildsystem.DependencyElement
import io.github.hdzitao.editstarters.dependency.Bom
import io.github.hdzitao.editstarters.dependency.Dependency
import io.github.hdzitao.editstarters.dependency.Repository
import io.github.hdzitao.editstarters.springboot.Starter
import io.github.hdzitao.editstarters.ui.ShowErrorException
import org.apache.commons.lang3.StringUtils
import org.jetbrains.kotlin.psi.*

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
            .map { it.getDependencyGroupArtifact() }
            .toList()
    }

    override fun KtBlockExpression.createDependencyTag(starter: Starter) {
        val instructions = dependencyInstruction(starter)
        for (instruction in instructions) {
            addExpression(instruction.toInstString("\${inst}(\"\${point}\")"))
        }
    }

    override fun KtFile.findOrCreateBomsTag(): KtBlockExpression {
        return TAG_BOM_MANAGEMENT.getOrCreateTopBlock().getOrCreateBlock(TAG_BOM_IMPORT)
    }

    override fun KtBlockExpression.findAllBoms(): List<Bom> {
        return findAllCallExpression(TAG_BOM)
            .map { tag ->
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
        return findAllCallExpression(TAG_REPOSITORY)
            .map { tag ->
                val arguments = tag.valueArguments
                val first = arguments.firstOrNull() ?: return@map EMPTY
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
        val statements = PsiTreeUtil.findChildrenOfAnyType(buildFile, KtScriptInitializer::class.java)
        val initializer = statements.find { regex.matches(it.text) }
        val expression = if (initializer == null) {
            buildFile.addExpression("$this {\n}") as KtCallExpression
        } else {
            PsiTreeUtil.findChildOfType(initializer, KtCallExpression::class.java)
        }

        return expression.getLambdaBodyExpression()
    }

    /**
     * 获取或创建闭包
     */
    private fun PsiElement.getOrCreateBlock(name: String): KtBlockExpression {
        var block = findCallExpression(name)
        if (block == null) {
            block = addExpression("$name {\n}") as KtCallExpression
        }

        return block.getLambdaBodyExpression()
    }

    /**
     * 查找语句
     */
    private fun PsiElement.findCallExpression(name: String): KtCallExpression? {
        val pattern = name.callNameRegex()
        val blocks = PsiTreeUtil.getChildrenOfTypeAsList(this, KtCallExpression::class.java)
        return blocks.find { pattern.matches(it.text) }
    }

    /**
     * 查找语句/批量
     */
    private fun PsiElement.findAllCallExpression(name: String): List<KtCallExpression> {
        val pattern = name.callNameRegex()
        val blocks = PsiTreeUtil.getChildrenOfTypeAsList(this, KtCallExpression::class.java)
        return blocks.filter { pattern.matches(it.text) }
    }

    private fun String.callNameRegex(): Regex {
        return "^$this\\W".toRegex()
    }

    /**
     * 第一参数
     */
    private fun KtCallExpression.getCallFirstParam(): String {
        val valueArguments = valueArguments
        if (valueArguments.isEmpty()) {
            return EMPTY
        }

        return valueArguments[0].trimQuotation()
    }

    /**
     * 获取依赖
     */
    private fun KtCallExpression.getDependencyGroupArtifact(): DependencyElement<KtCallExpression> {
        val namedArguments = valueArguments
            .filter { it.getArgumentName() != null }
            .associate { it.getArgumentName().trimQuotation() to it.getArgumentExpression().trimQuotation() }

        if (namedArguments.isEmpty()) {
            return newByGroupArtifact(getCallFirstParam()) { groupId, artifactId ->
                DependencyElement(groupId, artifactId, this)
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

        if (addEle.prevSibling != null && StringUtils.isNoneBlank(addEle.prevSibling.text)) {
            addEle.parent.addBefore(factory.createNewLine(1), addEle)
        }

        if (addEle.nextSibling != null && StringUtils.isNoneBlank(addEle.nextSibling.text)) {
            addEle.parent.addAfter(factory.createNewLine(1), addEle)
        }

        return addEle
    }

    private fun KtLambdaArgument.findCallLambdaStatement(leftName: String): KtBinaryExpression? {
        val statements = this.getLambdaExpression()?.bodyExpression?.statements
        if (statements.isNullOrEmpty()) {
            return null
        }

        return statements.find {
            if (it is KtBinaryExpression) {
                val left = it.left
                if (left != null) {
                    return@find leftName == left.text
                }
            }
            false
        } as KtBinaryExpression?
    }

    /**
     * 获取lambda体
     */
    private fun KtCallExpression?.getLambdaBodyExpression(): KtBlockExpression {
        // 创建不会为空
        if (this == null) {
            throw ShowErrorException.internal()
        }

        val lambdaArguments = this.lambdaArguments
        if (lambdaArguments.isEmpty()) {
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