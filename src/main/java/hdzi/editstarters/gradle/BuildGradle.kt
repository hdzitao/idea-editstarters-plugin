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
class BuildGradle(project: Project, private val buildFile: GroovyFile) : ProjectFile<GrClosableBlock>(), GradleSyntax {
    override fun getOrCreateDependenciesTag(): GrClosableBlock = getOrCreateClosure(buildFile, "dependencies")

    override fun findAllDependencies(dependenciesTag: GrClosableBlock): Sequence<ProjectDependency> =
        PsiTreeUtil.getChildrenOfTypeAsList(dependenciesTag, GrMethodCall::class.java).asSequence()
            .map {
                val (groupId, artifactId) = getMethodGroupName(it)
                ProjectDependency(groupId, artifactId, it)
            }

    override fun createDependencyTag(dependenciesTag: GrClosableBlock, info: StarterInfo) {
        val (instantiation, point) = dependencyInstruction(info)
        val statement = factory.createStatementFromText("$instantiation '$point'")
        dependenciesTag.addStatementBefore(statement, null)
    }

    override fun getOrCreateBomTag(): GrClosableBlock =
        getOrCreateClosure(getOrCreateClosure(buildFile, "dependencyManagement"), "imports")

    override fun findAllBom(bomTag: GrClosableBlock): Sequence<ProjectBom> =
        findAllMethod(bomTag, "mavenBom").asSequence()
            .map {
                val (groupId, artifactId) = getMethodGroupNameByFirstParam(it)
                ProjectBom(groupId, artifactId)
            }

    override fun createBomTag(bomTag: GrClosableBlock, bom: InitializrBom) {
        val (instantiation, point) = bomInstruction(bom)
        val statement = factory.createStatementFromText("$instantiation '$point'")
        bomTag.addStatementBefore(statement, null)
    }

    override fun getOrCreateRepositoriesTag(): GrClosableBlock = getOrCreateClosure(buildFile, "repositories")

    override fun findAllRepositories(repositoriesTag: GrClosableBlock): Sequence<ProjectRepository> =
        findAllMethod(repositoriesTag, "maven").asSequence()
            .map { ProjectRepository(getMethodFirstParam(findMethod(it.closureArguments[0], "url")) ?: "") }

    override fun createRepositoriesTag(repositoriesTag: GrClosableBlock, repository: InitializrRepository) {
        val (instantiation, point) = repositoryInstruction(repository)
        val statement = factory.createStatementFromText("$instantiation $point")
        repositoriesTag.addStatementBefore(statement, null)
    }

    private val factory = GroovyPsiElementFactory.getInstance(project)

    private fun getOrCreateClosure(element: PsiElement, name: String): GrClosableBlock {
        var closure = findMethod(element, name)
        if (closure == null) {
            val statement = factory.createStatementFromText("$name {\n}")
            closure = when (element) {
                is GrClosableBlock -> element.addStatementBefore(statement, null)
                else -> element.add(statement)
            } as GrMethodCall
        }

        return closure.closureArguments[0]
    }

    private fun findMethod(element: PsiElement, name: String): GrMethodCall? {
        return PsiTreeUtil.getChildrenOfTypeAsList(element, GrMethodCall::class.java)
            .find { name == it.invokedExpression.text }
    }

    private fun findAllMethod(element: PsiElement, name: String): List<GrMethodCall> {
        val closableBlocks = PsiTreeUtil.getChildrenOfTypeAsList(element, GrMethodCall::class.java)
        return ContainerUtil.findAll(closableBlocks) { name == it.invokedExpression.text }
    }

    private fun getMethodFirstParam(method: GrMethodCall?): String? {
        val originText = method?.argumentList?.allArguments?.get(0)?.text
        return trimText(originText)
    }

    private fun getMethodGroupNameByFirstParam(method: GrMethodCall): Pair<String, String> {
        return splitGroupName(getMethodFirstParam(method) ?: "")
    }

    private fun getMethodGroupName(method: GrMethodCall): Pair<String, String> {
        val namedArguments = method.namedArguments.associateBy(
            { trimText(it.label?.text) },
            { trimText(it.expression?.text) }
        )

        return if (namedArguments.isEmpty()) {
            getMethodGroupNameByFirstParam(method)
        } else {
            splitGroupName(namedArguments)
        }
    }

    private fun trimText(originText: String?) = originText?.trim('\'', '"')
}
