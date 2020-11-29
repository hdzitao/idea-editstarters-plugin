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
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.statements.blocks.GrClosableBlock
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall

/**
 * Created by taojinhou on 2019/1/16.
 */
class BuildGradle(project: Project, private val buildFile: GroovyFile) : GradleSyntax<GrClosableBlock>() {
    override fun getOrCreateDependenciesTag(): GrClosableBlock = buildFile.getOrCreateClosure("dependencies")

    override fun findAllDependencies(dependenciesTag: GrClosableBlock): Sequence<ProjectDependency> =
        PsiTreeUtil.getChildrenOfTypeAsList(dependenciesTag, GrMethodCall::class.java).asSequence()
            .map {
                val (groupId, artifactId) = it.getDependencyGroupArtifact()
                ProjectDependency(groupId, artifactId, it)
            }

    override fun createDependencyTag(dependenciesTag: GrClosableBlock, info: StarterInfo) {
        val instructions = dependencyInstruction(info)
        instructions.forEach {
            val (instruction, point) = it
            val statement = factory.createStatementFromText("$instruction '$point'")
            dependenciesTag.addStatementBefore(statement, null)
        }
    }

    override fun getOrCreateBomsTag(): GrClosableBlock =
        buildFile.getOrCreateClosure("dependencyManagement").getOrCreateClosure("imports")

    override fun findAllBoms(bomsTag: GrClosableBlock): Sequence<ProjectBom> =
        bomsTag.findAllMethod("mavenBom").asSequence()
            .map {
                val (groupId, artifactId) = splitGroupArtifact(it.getMethodFirstParam())
                ProjectBom(groupId, artifactId)
            }

    override fun createBomTag(bomsTag: GrClosableBlock, bom: InitializrBom) {
        val (instruction, point) = bomInstruction(bom)
        val statement = factory.createStatementFromText("$instruction '$point'")
        bomsTag.addStatementBefore(statement, null)
    }

    override fun getOrCreateRepositoriesTag(): GrClosableBlock = buildFile.getOrCreateClosure("repositories")

    override fun findAllRepositories(repositoriesTag: GrClosableBlock): Sequence<ProjectRepository> =
        repositoriesTag.findAllMethod("maven").asSequence()
            .map { ProjectRepository(it.closureArguments[0].findMethod("url")?.getMethodFirstParam() ?: "") }

    override fun createRepositoryTag(repositoriesTag: GrClosableBlock, repository: InitializrRepository) {
        val (instruction, point) = repositoryInstruction(repository)
        val statement = factory.createStatementFromText("$instruction { url '$point' }")
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

    private fun GrMethodCall.getDependencyGroupArtifact(): Pair<String, String> {
        val namedArguments = this.namedArguments.associateBy(
            { it.label?.text?.trimText() },
            { it.expression?.text?.trimText() }
        )

        return if (namedArguments.isEmpty()) {
            splitGroupArtifact(getMethodFirstParam())
        } else {
            Pair(namedArguments["group"] ?: "", namedArguments["name"] ?: "")
        }
    }

    private fun String.trimText(): String = trim('\'', '"')
}
