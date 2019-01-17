package hdzi.editstarters.gradle

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import hdzi.editstarters.ProjectFile
import hdzi.editstarters.bean.StarterInfo
import hdzi.editstarters.bean.initializr.InitializrBom
import hdzi.editstarters.bean.initializr.InitializrRepository
import hdzi.editstarters.bean.project.ProjectBom
import hdzi.editstarters.bean.project.ProjectDependency
import hdzi.editstarters.bean.project.ProjectRepository
import org.jetbrains.kotlin.psi.KtFile

/**
 * 参考 https://github.com/JetBrains/kotlin/blob/master/idea/idea-gradle/src/org/jetbrains/kotlin/idea/configuration/KotlinBuildScriptManipulator.kt
 *
 * Created by taojinhou on 2019/1/17.
 */
class BuildGradleKts(project: Project, private val buildFile: KtFile) : ProjectFile<PsiElement>() {
    override fun getOrCreateDependenciesTag(): PsiElement {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findAllDependencies(dependenciesTag: PsiElement): Sequence<ProjectDependency> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createDependencyTag(dependenciesTag: PsiElement, info: StarterInfo) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOrCreateBomTag(): PsiElement {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findAllBom(bomTag: PsiElement): Sequence<ProjectBom> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createBomTag(bomTag: PsiElement, bom: InitializrBom) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOrCreateRepositoriesTag(): PsiElement {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findAllRepositories(repositoriesTag: PsiElement): Sequence<ProjectRepository> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createRepositoriesTag(repositoriesTag: PsiElement, repository: InitializrRepository) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}