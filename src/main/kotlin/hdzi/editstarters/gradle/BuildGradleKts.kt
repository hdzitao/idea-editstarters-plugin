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
    override fun getOrCreateDependenciesTag(): KtBlockExpression = "dependencies".getOrCreateTopBlock()

    override fun findAllDependencies(dependenciesTag: KtBlockExpression): Sequence<ProjectDependency> =
        PsiTreeUtil.getChildrenOfTypeAsList(dependenciesTag, KtCallExpression::class.java).asSequence()
            .map {
                val (groupId, artifactId) = it.getCallGroupName()
                ProjectDependency(groupId, artifactId, it)
            }

    override fun createDependencyTag(dependenciesTag: KtBlockExpression, info: StarterInfo) {
        val (instantiation, point) = dependencyInstruction(info)
        dependenciesTag.addExpression("$instantiation(\"$point\")")
    }

    override fun getOrCreateBomsTag(): KtBlockExpression =
        "dependencyManagement".getOrCreateTopBlock().getOrCreateBlock("imports")

    override fun findAllBoms(bomsTag: KtBlockExpression): Sequence<ProjectBom> =
        bomsTag.findAllCallExpression("mavenBom").asSequence()
            .map {
                val (groupId, artifactId) = it.getCallGroupNameByFirstParam()
                ProjectBom(groupId, artifactId)
            }

    override fun createBomTag(bomsTag: KtBlockExpression, bom: InitializrBom) {
        val (instantiation, point) = bomInstruction(bom)
        bomsTag.addExpression("$instantiation(\"$point\")")
    }

    override fun getOrCreateRepositoriesTag(): KtBlockExpression = "repositories".getOrCreateTopBlock()

    override fun findAllRepositories(repositoriesTag: KtBlockExpression): Sequence<ProjectRepository> =
        repositoriesTag.findAllCallExpression("maven").asSequence()
            .map {
                var url = it.getCallFirstParam() ?: ""
                if (url.startsWith("{")) { // 传递的是个代码块
                    url = url.replace("""^.*\s+url\s*=\s*uri\("([^"]+)"\).*$""".toRegex(), "$1")
                }
                ProjectRepository(url)
            }

    override fun createRepositoryTag(repositoriesTag: KtBlockExpression, repository: InitializrRepository) {
        val (instantiation, point) = repositoryInstruction(repository)
        repositoriesTag.addExpression("$instantiation { url = uri(\"$point\") }")
    }

    override fun repositoryInstruction(repository: InitializrRepository) = GradleInstruction("maven", repository.url!!)

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


    private fun KtCallExpression.getCallGroupNameByFirstParam(): Pair<String, String> =
        splitGroupName(getCallFirstParam() ?: "")


    private fun KtCallExpression.getCallGroupName(): Pair<String, String> {
        val namedArguments = this.valueArguments.associateBy(
            { it.getArgumentName()?.text?.trimText() },
            { it.getArgumentExpression()?.text?.trimText() })
            .filter { it.key != null }

        return if (namedArguments.isEmpty()) {
            getCallGroupNameByFirstParam()
        } else {
            splitGroupName(namedArguments)
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