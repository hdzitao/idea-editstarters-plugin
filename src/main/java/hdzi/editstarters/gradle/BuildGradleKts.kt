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
class BuildGradleKts(project: Project, private val buildFile: KtFile) : ProjectFile<KtBlockExpression>() {
    override fun getOrCreateDependenciesTag(): KtBlockExpression = getOrCreateTopBlock("dependencies")

    override fun findAllDependencies(dependenciesTag: KtBlockExpression): Sequence<ProjectDependency> =
        PsiTreeUtil.getChildrenOfTypeAsList(dependenciesTag, KtCallExpression::class.java).asSequence()
            .map {
                val (groupId, artifactId) = getGroupName(it)
                ProjectDependency(groupId, artifactId, it)
            }

    override fun createDependencyTag(dependenciesTag: KtBlockExpression, info: StarterInfo) {
        val instantiation = Scope.map(info.scope)
        val starter = "${info.groupId}:${info.artifactId}"
        val version = if (info.version != null) ":${info.version}" else ""
        dependenciesTag.addExpression("$instantiation(\"$starter$version\")")
    }

    override fun getOrCreateBomTag(): KtBlockExpression =
        getOrCreateBlock(getOrCreateTopBlock("dependencyManagement"), "imports")

    override fun findAllBom(bomTag: KtBlockExpression): Sequence<ProjectBom> =
        findAllMethod(bomTag, "mavenBom").asSequence()
            .map {
                val (groupId, artifactId) = getGroupNameByFirstParam(it)
                ProjectBom(groupId, artifactId)
            }

    override fun createBomTag(bomTag: KtBlockExpression, bom: InitializrBom) {
        val instantiation = "mavenBom"
        val point = "${bom.groupId}:${bom.artifactId}"
        val version = if (bom.version != null) ":${bom.version}" else ""
        bomTag.addExpression("$instantiation(\"$point$version\")")
    }

    override fun getOrCreateRepositoriesTag(): KtBlockExpression = getOrCreateTopBlock("repositories")

    override fun findAllRepositories(repositoriesTag: KtBlockExpression): Sequence<ProjectRepository> =
        findAllMethod(repositoriesTag, "maven").asSequence()
            .map {
                ProjectRepository(getMethodFirstParam(it) ?: "")
            }

    override fun createRepositoriesTag(repositoriesTag: KtBlockExpression, repository: InitializrRepository) {
        repositoriesTag.addExpression("maven(\"${repository.url}\")")
    }

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
        var block = findMethod(element, name)
        if (block == null) {
            block = element.addExpression("$name {\n}") as KtCallExpression
        }

        return (block.lambdaArguments[0].getArgumentExpression() as KtLambdaExpression).bodyExpression!!
    }

    private fun findMethod(element: PsiElement, name: String): KtCallExpression? {
        val regex = callNameRegex(name)
        val closableBlocks = PsiTreeUtil.getChildrenOfTypeAsList(element, KtCallExpression::class.java)
        return ContainerUtil.find<KtCallExpression, KtCallExpression>(closableBlocks) { call ->
            regex.find(call.text) != null
        }
    }

    private fun findAllMethod(element: PsiElement, name: String): List<KtCallExpression> {
        val regex = callNameRegex(name)
        val closableBlocks = PsiTreeUtil.getChildrenOfTypeAsList(element, KtCallExpression::class.java)
        return ContainerUtil.findAll(closableBlocks) { call ->
            regex.find(call.text) != null
        }
    }

    private fun callNameRegex(name: String) = "^$name\\W".toRegex()

    private fun getMethodFirstParam(method: KtCallExpression?): String? {
        val originText = method?.valueArguments?.get(0)?.getArgumentExpression()?.text
        return trimText(originText)
    }

    private fun getGroupNameByFirstParam(method: KtCallExpression): Pair<String, String> {
        val param = getMethodFirstParam(method) ?: ""
        val group = "^([^:]+):([^:]+)".toRegex().find(param)?.groupValues
        return Pair(group?.get(1) ?: "", group?.get(2) ?: "")
    }

    private fun getGroupName(method: KtCallExpression): Pair<String, String> {
        val arguments = method.valueArguments
        return if (arguments.size == 1) {
            return getGroupNameByFirstParam(method)
        } else {
            val map = arguments.associateBy(
                { trimText(it.getArgumentName()!!.text) },
                { trimText(it.getArgumentExpression()!!.text) })

            Pair(map["group"] ?: "", map["name"] ?: "")
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