package hdzi.editstarters.buildsystem.gradle

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.containers.ContainerUtil
import hdzi.editstarters.buildsystem.ProjectBom
import hdzi.editstarters.buildsystem.ProjectDependency
import hdzi.editstarters.buildsystem.ProjectFile
import hdzi.editstarters.buildsystem.ProjectRepository
import hdzi.editstarters.springboot.initializr.InitializrBom
import hdzi.editstarters.springboot.initializr.InitializrRepository
import hdzi.editstarters.springboot.initializr.StarterInfo
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.statements.blocks.GrClosableBlock
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall

/**
 * Created by taojinhou on 2019/1/16.
 */
class BuildGradle(project: Project, private val buildFile: GroovyFile) : ProjectFile<GrClosableBlock>() {
    override fun getOrCreateDependenciesTag(): GrClosableBlock = buildFile.getOrCreateClosure("dependencies")

    override fun findAllDependencies(dependenciesTag: GrClosableBlock): Sequence<ProjectDependency> =
        PsiTreeUtil.getChildrenOfTypeAsList(dependenciesTag, GrMethodCall::class.java).asSequence()
            .map {
                val (groupId, artifactId) = it.getMethodGroupName()
                ProjectDependency(groupId, artifactId, it)
            }

    override fun createDependencyTag(dependenciesTag: GrClosableBlock, info: StarterInfo) {
        val scopeMethod = mapScope(info.scope)
        val version = if (info.version != null) ":${info.version}" else ""
        val statement = factory.createStatementFromText("$scopeMethod '${info.groupId}:${info.artifactId}$version'")
        dependenciesTag.addStatementBefore(statement, null)
    }

    override fun getOrCreateBomsTag(): GrClosableBlock =
        buildFile.getOrCreateClosure("dependencyManagement").getOrCreateClosure("imports")

    override fun findAllBoms(bomsTag: GrClosableBlock): Sequence<ProjectBom> =
        bomsTag.findAllMethod("mavenBom").asSequence()
            .map {
                val (groupId, artifactId) = it.getMethodGroupNameByFirstParam()
                ProjectBom(groupId, artifactId)
            }

    override fun createBomTag(bomsTag: GrClosableBlock, bom: InitializrBom) {
        val statement = factory.createStatementFromText("mavenBom '${bom.groupId}:${bom.artifactId}:${bom.version}'")
        bomsTag.addStatementBefore(statement, null)
    }

    override fun getOrCreateRepositoriesTag(): GrClosableBlock = buildFile.getOrCreateClosure("repositories")

    override fun findAllRepositories(repositoriesTag: GrClosableBlock): Sequence<ProjectRepository> =
        repositoriesTag.findAllMethod("maven").asSequence()
            .map { ProjectRepository(it.closureArguments[0].findMethod("url")?.getMethodFirstParam() ?: "") }

    override fun createRepositoryTag(repositoriesTag: GrClosableBlock, repository: InitializrRepository) {
        val statement = factory.createStatementFromText("maven { url '${repository.url}' }")
        repositoriesTag.addStatementBefore(statement, null)
    }

    private val factory = GroovyPsiElementFactory.getInstance(project)

    private fun PsiElement.getOrCreateClosure(name: String): GrClosableBlock {
        var closure = findMethod(name)
        if (closure == null) {
            val statement = factory.createStatementFromText("$name {\n}")
            closure = when (this) {
                is GrClosableBlock -> addStatementBefore(statement, null)
                else -> add(statement)
            } as GrMethodCall
        }

        return closure.closureArguments[0]
    }

    private fun PsiElement.findMethod(name: String): GrMethodCall? =
        PsiTreeUtil.getChildrenOfTypeAsList(this, GrMethodCall::class.java)
            .find { name == it.invokedExpression.text }

    private fun PsiElement.findAllMethod(name: String): List<GrMethodCall> {
        val closableBlocks = PsiTreeUtil.getChildrenOfTypeAsList(this, GrMethodCall::class.java)
        return ContainerUtil.findAll(closableBlocks) { name == it.invokedExpression.text }
    }

    private fun GrMethodCall.getMethodFirstParam(): String? =
        this.argumentList.allArguments[0]?.text?.trimText()

    private fun GrMethodCall.getMethodGroupNameByFirstParam(): Pair<String, String> {
        val group = "^([^:]+):([^:]+)".toRegex().find(getMethodFirstParam() ?: "")?.groupValues
        return Pair(group?.get(1) ?: "", group?.get(2) ?: "")
    }

    private fun GrMethodCall.getMethodGroupName(): Pair<String, String> {
        val namedArguments = this.namedArguments.associateBy(
            { it.label?.text?.trimText() },
            { it.expression?.text?.trimText() }
        )

        return if (namedArguments.isEmpty()) {
            getMethodGroupNameByFirstParam()
        } else {
            Pair(namedArguments["group"] ?: "", namedArguments["name"] ?: "")
        }
    }

    private fun String.trimText(): String = trim('\'', '"')
}
