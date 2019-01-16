package hdzi.editstarters.gradle

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.containers.ContainerUtil
import hdzi.editstarters.DependSupport
import hdzi.editstarters.ProjectFile
import hdzi.editstarters.bean.StarterInfo
import hdzi.editstarters.bean.initializr.InitializrBom
import hdzi.editstarters.bean.initializr.InitializrRepository
import hdzi.editstarters.bean.project.ProjectBom
import hdzi.editstarters.bean.project.ProjectDependency
import hdzi.editstarters.bean.project.ProjectRepository
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.statements.GrStatement
import org.jetbrains.plugins.groovy.lang.psi.api.statements.blocks.GrClosableBlock
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall

/**
 * Created by taojinhou on 2019/1/16.
 */
class BuildGradle(project: Project, private val buildFile: PsiFile) : ProjectFile, DependSupport {
    private val factory = GroovyPsiElementFactory.getInstance(project)

    override fun removeDependencies(dependencies: Collection<StarterInfo>) {
        val removeDeps = dependencies.map { it.point }.toSet()
        val dependenciesClosure = getOrCreateClosure(buildFile, "dependencies")
        val depRegex = "^([^:]+:[^:]+).*$".toRegex()
        PsiTreeUtil.getChildrenOfTypeAsList(dependenciesClosure, GrMethodCall::class.java).forEach {
            val extDep = getMethodFirstStringParam(it) ?: ""
            val match = depRegex.find(extDep)
            if (match != null) {
                val (groupId, artifactId) = match.destructured
                if (removeDeps.contains(ProjectDependency(groupId, artifactId).point)) {
                    it.removeStatement()
                }
            }
        }
    }

    override fun addDependencies(dependencies: Collection<StarterInfo>) {
        val dependenciesClosure = getOrCreateClosure(buildFile, "dependencies")

        for (depend in dependencies) {
            dependenciesClosure.addStatementBefore(getDependStatement(depend), null)
            if (depend.bom != null) {
                addBom(depend.bom!!)
            }

            if (!depend.repositories.isEmpty()) {
                addRepositories(depend.repositories)
            }
        }
    }

    override fun addRepositories(repositories: Set<InitializrRepository>) {
        val repositoriesClosure = getOrCreateClosure(buildFile, "repositories")
        val mavenMethods = findAllBlock(repositoriesClosure, "maven")
        val extRepositories = mavenMethods.asSequence()
            .map { ProjectRepository(getMethodFirstStringParam(findBlock(it.closureArguments[0], "url")) ?: "").point }
            .toSet()

        repositories.asSequence()
            .filter {
                //去重
                !extRepositories.contains(it.point)
            }.forEach { repo ->
                repositoriesClosure.addStatementBefore(getRepoStatement(repo), null)
            }
    }

    override fun addBom(bom: InitializrBom) {
        val bomClosure = getOrCreateClosure(buildFile, "dependencyManagement")
        val importsClosure = getOrCreateClosure(bomClosure, "imports")

        // 去重
        val gradleBomRegex = "^([^:]+):(^[:]+)".toRegex()
        findAllBlock(importsClosure, "mavenBom").forEach { mavenBom ->
            val match = gradleBomRegex.find(getMethodFirstStringParam(mavenBom) ?: "")
            if (match != null) {
                val (groupId, artifactId) = match.destructured
                if (bom.point == ProjectBom(groupId, artifactId).point) {
                    return
                }
            }
        }

        importsClosure.addStatementBefore(getBomStatement(bom), null)
    }

    private fun getDependStatement(depend: StarterInfo): GrStatement {
        val instantiation = Scope.map(depend.scope)
        val starter = "${depend.groupId}:${depend.artifactId}"
        val version = if (depend.version != null) ":${depend.version}" else ""

        return factory.createStatementFromText("$instantiation '$starter$version'")
    }

    private fun getRepoStatement(repo: InitializrRepository) =
        factory.createStatementFromText("maven { url '${repo.url}' }")

    private fun getBomStatement(bom: InitializrBom): GrStatement {
        val instantiation = "mavenBom"
        val point = "${bom.groupId}:${bom.artifactId}"
        val version = if (bom.version != null) ":${bom.version}" else ""

        return factory.createStatementFromText("$instantiation '$point$version'")
    }


    private fun getOrCreateClosure(element: PsiElement, name: String): GrClosableBlock {
        var block = findBlock(element, name)
        if (block == null) {
            val statement = factory.createStatementFromText("$name {\n}")
            when (element) {
                is GrClosableBlock -> element.addStatementBefore(statement, null)
                else -> element.add(statement)
            }
            block = findBlock(element, name)
        }

        return block!!.closureArguments[0]
    }

    private fun findBlock(element: PsiElement, name: String): GrMethodCall? {
        val closableBlocks = PsiTreeUtil.getChildrenOfTypeAsList(element, GrMethodCall::class.java)
        return ContainerUtil.find<GrMethodCall, GrMethodCall>(closableBlocks) { call ->
            name == call.invokedExpression.text
        }
    }

    private fun findAllBlock(element: PsiElement, name: String): MutableList<out GrMethodCall> {
        val closableBlocks = PsiTreeUtil.getChildrenOfTypeAsList(element, GrMethodCall::class.java)
        return ContainerUtil.findAll(closableBlocks) { call ->
            name == call.invokedExpression.text
        }
    }

    private fun getMethodFirstStringParam(method: GrMethodCall?): String? {
        val originText = method?.argumentList?.allArguments?.get(0)?.text
        return originText?.trim('\'', '"')
    }
}