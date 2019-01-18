package hdzi.editstarters.gradle

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.containers.ContainerUtil
import hdzi.editstarters.ProjectFile
import hdzi.editstarters.bean.StarterInfo
import hdzi.editstarters.bean.initializr.InitializrBom
import hdzi.editstarters.bean.initializr.InitializrRepository
import hdzi.editstarters.bean.project.ProjectBom
import hdzi.editstarters.bean.project.ProjectDependency
import hdzi.editstarters.bean.project.ProjectRepository
import org.jetbrains.kotlin.psi.*

/**
 * 参考 https://github.com/JetBrains/kotlin/blob/master/idea/idea-gradle/src/org/jetbrains/kotlin/idea/configuration/KotlinBuildScriptManipulator.kt
 *
 * Created by taojinhou on 2019/1/17.
 */
class BuildGradleKts(project: Project, private val buildFile: KtFile) : ProjectFile<KtBlockExpression>(), GradleSyntax {
    override fun getOrCreateDependenciesTag(): KtBlockExpression = getOrCreateTopBlock("dependencies")

    override fun findAllDependencies(dependenciesTag: KtBlockExpression): Sequence<ProjectDependency> =
        PsiTreeUtil.getChildrenOfTypeAsList(dependenciesTag, KtCallExpression::class.java).asSequence()
            .map {
                val (groupId, artifactId) = getCallGroupName(it)
                ProjectDependency(groupId, artifactId, it)
            }

    override fun createDependencyTag(dependenciesTag: KtBlockExpression, info: StarterInfo) {
        val (instantiation, point) = dependencyInstruction(info)
        dependenciesTag.addExpression("$instantiation(\"$point\")")
    }

    override fun getOrCreateBomTag(): KtBlockExpression =
        getOrCreateBlock(getOrCreateTopBlock("dependencyManagement"), "imports")

    override fun findAllBom(bomTag: KtBlockExpression): Sequence<ProjectBom> =
        findAllCallExpression(bomTag, "mavenBom").asSequence()
            .map {
                val (groupId, artifactId) = getCallGroupNameByFirstParam(it)
                ProjectBom(groupId, artifactId)
            }

    override fun createBomTag(bomTag: KtBlockExpression, bom: InitializrBom) {
        val (instantiation, point) = bomInstruction(bom)
        bomTag.addExpression("$instantiation(\"$point\")")
    }

    override fun getOrCreateRepositoriesTag(): KtBlockExpression = getOrCreateTopBlock("repositories")

    override fun findAllRepositories(repositoriesTag: KtBlockExpression): Sequence<ProjectRepository> =
        findAllCallExpression(repositoriesTag, "maven").asSequence()
            .map { ProjectRepository(getCallFirstParam(it) ?: "") }

    override fun createRepositoriesTag(repositoriesTag: KtBlockExpression, repository: InitializrRepository) {
        val (instantiation, point) = repositoryInstruction(repository)
        repositoriesTag.addExpression("$instantiation(\"$point\")")
    }

    override fun repositoryInstruction(repository: InitializrRepository) =
        GradleInstruction("maven", repository.url!!)

    private val factory = KtPsiFactory(project)

    private fun getOrCreateTopBlock(name: String): KtBlockExpression {
        val regex = callNameRegex(name)
        val initializer = PsiTreeUtil.findChildrenOfAnyType(buildFile, KtScriptInitializer::class.java).find {
            regex.find(it.text) != null
        }

        return (if (initializer == null) {
            buildFile.addExpression("$name {\n}") as KtCallExpression
        } else {
            PsiTreeUtil.findChildOfType(initializer, KtCallExpression::class.java)
        }!!.lambdaArguments[0].getArgumentExpression() as KtLambdaExpression).bodyExpression!!
    }

    private fun getOrCreateBlock(element: PsiElement, name: String): KtBlockExpression {
        var block = findCallExpression(element, name)
        if (block == null) {
            block = element.addExpression("$name {\n}") as KtCallExpression
        }

        return (block.lambdaArguments[0].getArgumentExpression() as KtLambdaExpression).bodyExpression!!
    }

    private fun findCallExpression(element: PsiElement, name: String): KtCallExpression? {
        val regex = callNameRegex(name)
        return PsiTreeUtil.getChildrenOfTypeAsList(element, KtCallExpression::class.java)
            .find { regex.find(it.text) != null }
    }

    private fun findAllCallExpression(element: PsiElement, name: String): List<KtCallExpression> {
        val regex = callNameRegex(name)
        val blocks = PsiTreeUtil.getChildrenOfTypeAsList(element, KtCallExpression::class.java)
        return ContainerUtil.findAll(blocks) { regex.find(it.text) != null }
    }

    private fun callNameRegex(name: String) = "^$name\\W".toRegex()

    private fun getCallFirstParam(method: KtCallExpression?): String? {
        val originText = method?.valueArguments?.get(0)?.getArgumentExpression()?.text
        return trimText(originText)
    }

    private fun getCallGroupNameByFirstParam(method: KtCallExpression): Pair<String, String> {
        return splitGroupName(getCallFirstParam(method) ?: "")
    }

    private fun getCallGroupName(method: KtCallExpression): Pair<String, String> {
        val namedArguments = method.valueArguments.associateBy(
            { trimText(it.getArgumentName()?.text) },
            { trimText(it.getArgumentExpression()?.text) })
            .filter { it.key != null }

        return if (namedArguments.isEmpty()) {
            getCallGroupNameByFirstParam(method)
        } else {
            splitGroupName(namedArguments)
        }
    }

    private fun trimText(originText: String?) = originText?.trim('"')

    private fun PsiElement.addExpression(text: String) =
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