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
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.statements.blocks.GrClosableBlock
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall

/**
 * Created by taojinhou on 2019/1/16.
 */
class BuildGradle(project: Project, private val buildFile: GroovyFile) : ProjectFile<GrClosableBlock>() {
    override fun getOrCreateDependenciesTag(): GrClosableBlock = getOrCreateClosure(buildFile, "dependencies")

    override fun findAllDependencies(dependenciesTag: GrClosableBlock): Sequence<ProjectDependency> =
        PsiTreeUtil.getChildrenOfTypeAsList(dependenciesTag, GrMethodCall::class.java).asSequence()
            .map {
                val (groupId, artifactId) = getGroupName(it)
                ProjectDependency(groupId, artifactId, it)
            }

    override fun createDependencyTag(dependenciesTag: GrClosableBlock, info: StarterInfo) {
        val instantiation = Scope.map(info.scope)
        val starter = "${info.groupId}:${info.artifactId}"
        val version = if (info.version != null) ":${info.version}" else ""
        val statement = factory.createStatementFromText("$instantiation '$starter$version'")
        dependenciesTag.addStatementBefore(statement, null)
    }

    override fun getOrCreateBomTag(): GrClosableBlock =
        getOrCreateClosure(getOrCreateClosure(buildFile, "dependencyManagement"), "imports")

    override fun findAllBom(bomTag: GrClosableBlock): Sequence<ProjectBom> =
        findAllMethod(bomTag, "mavenBom").asSequence()
            .map {
                val (groupId, artifactId) = getGroupNameByFirstParam(it)
                ProjectBom(groupId, artifactId)
            }

    override fun createBomTag(bomTag: GrClosableBlock, bom: InitializrBom) {
        val instantiation = "mavenBom"
        val point = "${bom.groupId}:${bom.artifactId}"
        val version = if (bom.version != null) ":${bom.version}" else ""
        val statement = factory.createStatementFromText("$instantiation '$point$version'")
        bomTag.addStatementBefore(statement, null)
    }

    override fun getOrCreateRepositoriesTag(): GrClosableBlock = getOrCreateClosure(buildFile, "repositories")

    override fun findAllRepositories(repositoriesTag: GrClosableBlock): Sequence<ProjectRepository> =
        findAllMethod(repositoriesTag, "maven").asSequence()
            .map { ProjectRepository(getMethodFirstParam(findMethod(it.closureArguments[0], "url")) ?: "") }

    override fun createRepositoriesTag(repositoriesTag: GrClosableBlock, repository: InitializrRepository) {
        val statement = factory.createStatementFromText("maven { url '${repository.url}' }")
        repositoriesTag.addStatementBefore(statement, null)
    }

    private val factory = GroovyPsiElementFactory.getInstance(project)

    private fun getOrCreateClosure(element: PsiElement, name: String): GrClosableBlock {
        var block = findMethod(element, name)
        if (block == null) {
            val statement = factory.createStatementFromText("$name {\n}")
            when (element) {
                is GrClosableBlock -> element.addStatementBefore(statement, null)
                else -> element.add(statement)
            }
            block = findMethod(element, name)
        }

        return block!!.closureArguments[0]
    }

    private fun findMethod(element: PsiElement, name: String): GrMethodCall? {
        val closableBlocks = PsiTreeUtil.getChildrenOfTypeAsList(element, GrMethodCall::class.java)
        return ContainerUtil.find<GrMethodCall, GrMethodCall>(closableBlocks) { call ->
            name == call.invokedExpression.text
        }
    }

    private fun findAllMethod(element: PsiElement, name: String): MutableList<out GrMethodCall> {
        val closableBlocks = PsiTreeUtil.getChildrenOfTypeAsList(element, GrMethodCall::class.java)
        return ContainerUtil.findAll(closableBlocks) { call ->
            name == call.invokedExpression.text
        }
    }

    private fun getMethodFirstParam(method: GrMethodCall?): String? {
        val originText = method?.argumentList?.allArguments?.get(0)?.text
        return trimText(originText)
    }

    private fun getGroupNameByFirstParam(method: GrMethodCall): Pair<String, String> {
        val param = getMethodFirstParam(method) ?: ""
        val group = "^([^:]+):([^:]+)".toRegex().find(param)?.groupValues
        return Pair(group?.get(1) ?: "", group?.get(2) ?: "")
    }

    private fun getGroupName(method: GrMethodCall): Pair<String, String> {
        val namedArguments = method.namedArguments
        return if (namedArguments.isEmpty()) {
            return getGroupNameByFirstParam(method)
        } else {
            val map = namedArguments.associateBy({ trimText(it.label?.text) }, { trimText(it.expression?.text) })
            Pair(map["group"] ?: "", map["name"] ?: "")
        }
    }

    private fun trimText(originText: String?) = originText?.trim('\'', '"')
}
