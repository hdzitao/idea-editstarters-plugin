package hdzi.editstarters.buildsystem.gradle

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.containers.ContainerUtil
import hdzi.editstarters.buildsystem.ProjectBom
import hdzi.editstarters.buildsystem.ProjectDependency
import hdzi.editstarters.buildsystem.ProjectRepository
import hdzi.editstarters.springboot.initializr.InitializrBom
import hdzi.editstarters.springboot.initializr.InitializrRepository
import hdzi.editstarters.springboot.initializr.StarterInfo
import org.jetbrains.kotlin.psi.*

/**
 * 参考 https://github.com/JetBrains/kotlin/blob/master/idea/idea-gradle/src/org/jetbrains/kotlin/idea/configuration/KotlinBuildScriptManipulator.kt
 *
 * Created by taojinhou on 2019/1/17.
 */
class BuildGradleKts(project: Project, private val buildFile: KtFile) : GradleSyntax<KtBlockExpression>() {
    override fun getOrCreateDependenciesTag(): KtBlockExpression = "dependencies".getOrCreateTopBlock()

    override fun findAllDependencies(dependenciesTag: KtBlockExpression): Sequence<ProjectDependency> =
        PsiTreeUtil.getChildrenOfTypeAsList(dependenciesTag, KtCallExpression::class.java).asSequence()
            .map {
                val (groupId, artifactId) = it.getDependencyGroupArtifact()
                ProjectDependency(groupId, artifactId, it)
            }

    override fun createDependencyTag(dependenciesTag: KtBlockExpression, info: StarterInfo) {
        val instructions = dependencyInstruction(info)
        instructions.forEach {
            val (instruction, point) = it
            dependenciesTag.addExpression("$instruction(\"$point\")")
        }
    }

    override fun getOrCreateBomsTag(): KtBlockExpression =
        "dependencyManagement".getOrCreateTopBlock().getOrCreateBlock("imports")

    override fun findAllBoms(bomsTag: KtBlockExpression): Sequence<ProjectBom> =
        bomsTag.findAllCallExpression("mavenBom").asSequence()
            .map {
                val (groupId, artifactId) = splitGroupArtifact(it.getCallFirstParam())
                ProjectBom(groupId, artifactId)
            }

    override fun createBomTag(bomsTag: KtBlockExpression, bom: InitializrBom) {
        val (instruction, point) = bomInstruction(bom)
        bomsTag.addExpression("$instruction(\"$point\")")
    }

    override fun getOrCreateRepositoriesTag(): KtBlockExpression = "repositories".getOrCreateTopBlock()

    override fun findAllRepositories(repositoriesTag: KtBlockExpression): Sequence<ProjectRepository> =
        repositoriesTag.findAllCallExpression("maven").asSequence()
            .map {
                val arguments = it.valueArguments
                val url = if (arguments.isNotEmpty()) {
                    when (val first = arguments.first()) {
                        is KtLambdaArgument -> {
                            val statements = first.getLambdaExpression()?.bodyExpression?.statements
                            val urlStatement = statements?.find { statement ->
                                statement is KtBinaryExpression
                                        && statement.left?.text == "url"
                            }
                            ((urlStatement as KtBinaryExpression).right as? KtCallExpression)?.getCallFirstParam()
                        }
                        is KtValueArgument -> {
                            it.getCallFirstParam()
                        }
                        else -> null
                    }
                } else null
                ProjectRepository(url ?: "")
            }

    override fun createRepositoryTag(repositoriesTag: KtBlockExpression, repository: InitializrRepository) {
        val (instruction, point) = repositoryInstruction(repository)
        repositoriesTag.addExpression("$instruction { url = uri(\"$point\") }")
    }

    private val factory = KtPsiFactory(project)

    private fun String.getOrCreateTopBlock(): KtBlockExpression {
        val regex = callNameRegex()
        val initializer = PsiTreeUtil.findChildrenOfAnyType(buildFile, KtScriptInitializer::class.java).find {
            regex.find(it.text) != null
        }

        return (if (initializer == null) {
            buildFile.addExpression("$this {\n}") as KtCallExpression
        } else {
            PsiTreeUtil.findChildOfType(initializer, KtCallExpression::class.java)
        }!!.lambdaArguments[0].getArgumentExpression() as KtLambdaExpression).bodyExpression!!
    }

    private fun PsiElement.getOrCreateBlock(name: String): KtBlockExpression {
        var block = findCallExpression(name)
        if (block == null) {
            block = addExpression("$name {\n}") as KtCallExpression
        }

        return (block.lambdaArguments[0].getArgumentExpression() as KtLambdaExpression).bodyExpression!!
    }

    private fun PsiElement.findCallExpression(name: String): KtCallExpression? {
        val regex = name.callNameRegex()
        return PsiTreeUtil.getChildrenOfTypeAsList(this, KtCallExpression::class.java)
            .find { regex.find(it.text) != null }
    }

    private fun PsiElement.findAllCallExpression(name: String): List<KtCallExpression> {
        val regex = name.callNameRegex()
        val blocks = PsiTreeUtil.getChildrenOfTypeAsList(this, KtCallExpression::class.java)
        return ContainerUtil.findAll(blocks) { regex.find(it.text) != null }
    }

    private fun String.callNameRegex() = "^$this\\W".toRegex()

    private fun KtCallExpression.getCallFirstParam(): String? =
        this.valueArguments[0]?.getArgumentExpression()?.text?.trimText()


    private fun KtCallExpression.getDependencyGroupArtifact(): Pair<String, String> {
        val namedArguments = this.valueArguments.associateBy(
            { it.getArgumentName()?.text?.trimText() },
            { it.getArgumentExpression()?.text?.trimText() }
        ).filter { it.key != null }

        return if (namedArguments.isEmpty()) {
            splitGroupArtifact(getCallFirstParam())
        } else {
            Pair(namedArguments["group"] ?: "", namedArguments["name"] ?: "")
        }
    }

    private fun String.trimText() = this.trim('"')

    private fun PsiElement.addExpression(text: String): PsiElement =
        add(factory.createExpression(text)).apply {
            // 是否要加换行符
            if (prevSibling != null && prevSibling.text.isNotBlank()) {
                parent.addBefore(factory.createNewLine(1), this)
            }

            if (nextSibling != null && nextSibling.text.isNotBlank()) {
                parent.addAfter(factory.createNewLine(1), this)
            }
        }
}