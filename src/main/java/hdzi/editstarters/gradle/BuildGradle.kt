package hdzi.editstarters.gradle

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.containers.ContainerUtil
import hdzi.editstarters.springboot.ProjectFile
import hdzi.editstarters.springboot.bean.DepResponse
import hdzi.editstarters.springboot.bean.StarterInfo
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.statements.GrStatement
import org.jetbrains.plugins.groovy.lang.psi.api.statements.blocks.GrClosableBlock
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall

/**
 * Created by taojinhou on 2019/1/16.
 */
class BuildGradle(val project: Project, val buildFile: PsiFile) : ProjectFile {

    override fun removeDependencies(dependencies: Collection<StarterInfo>) {
        val removeDeps = dependencies.map { it.point }.toSet()
        val dependenciesClosure = getOrCreateClosure(buildFile, "dependencies", null)
        PsiTreeUtil.getChildrenOfTypeAsList(dependenciesClosure, GrMethodCall::class.java).forEach {
            val extDep = getMethodFirstStringParam(it)
            val point = extDep?.replace("^([^:]+:[^:]+).*$".toRegex(), "$1")
            if (removeDeps.contains(point)) it.removeStatement()
        }
    }

    override fun addDependencies(dependencies: Collection<StarterInfo>) {
        val factory = GroovyPsiElementFactory.getInstance(project)
        val dependenciesClosure = getOrCreateClosure(buildFile, "dependencies", factory)

        for (depend in dependencies) {
            dependenciesClosure.addStatementBefore(getDependStatement(depend, factory), null)
            if (depend.bom != null) {
                addBom(depend.bom!!, factory)
            }

            if (!depend.repositories.isEmpty()) {
                addRepositories(depend.repositories, factory)
            }
        }
    }

    private fun addRepositories(repositories: Set<DepResponse.Repository>, factory: GroovyPsiElementFactory) {
        val repositoriesClosure = getOrCreateClosure(buildFile, "repositories", factory)
        val mavenMethods = findAllBlock(repositoriesClosure, "maven")
        val extRepositories = mavenMethods.asSequence()
            .map { getMethodFirstStringParam(findBlock(it.closureArguments[0], "url")) }
            .filter { it != null }
            .toSet()

        repositories.asSequence()
            .filter {
                //去重
                !extRepositories.contains(it.url)
            }.forEach { repo ->
                repositoriesClosure.addStatementBefore(getRepoStatement(repo, factory), null)
            }
    }

    private fun addBom(bom: DepResponse.Bom, factory: GroovyPsiElementFactory) {
        val bomClosure = getOrCreateClosure(buildFile, "dependencyManagement", factory)
        val importsClosure = getOrCreateClosure(bomClosure, "imports", factory)

        // 去重
        findAllBlock(importsClosure, "mavenBom").forEach { mavenBom ->
            val text = getMethodFirstStringParam(mavenBom)!!
            if (text.startsWith("${bom.groupId}:${bom.artifactId}")) return
        }

        importsClosure.addStatementBefore(getBomStatement(bom, factory), null)
    }

    private fun getDependStatement(depend: StarterInfo, factory: GroovyPsiElementFactory): GrStatement {
        val instantiation = Scope.mapScope(depend.scope)
        val point = "${depend.groupId}:${depend.artifactId}"
        val version = if (depend.version != null) ":${depend.version}" else ""

        return factory.createStatementFromText("${instantiation} '${point}${version}'")
    }

    private fun getRepoStatement(repo: DepResponse.Repository, factory: GroovyPsiElementFactory) =
        factory.createStatementFromText("maven { url '${repo.url}' }")

    private fun getBomStatement(bom: DepResponse.Bom, factory: GroovyPsiElementFactory): GrStatement {
        val instantiation = "mavenBom"
        val point = "${bom.groupId}:${bom.artifactId}"
        val version = if (bom.version != null) ":${bom.version}" else ""

        return factory.createStatementFromText("${instantiation} '${point}${version}'")
    }


    private fun getOrCreateClosure(
        element: PsiElement,
        name: String,
        factory: GroovyPsiElementFactory?
    ): GrClosableBlock {
        var block = findBlock(element, name)
        if (block == null) {
            val statement = factory!!.createStatementFromText("${name} {\n}")
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