package hdzi.editstarters.gradle

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import hdzi.editstarters.DependSupport
import hdzi.editstarters.ProjectFile
import hdzi.editstarters.bean.StarterInfo
import hdzi.editstarters.bean.initializr.InitializrBom
import hdzi.editstarters.bean.initializr.InitializrRepository

/**
 * 参考 https://github.com/JetBrains/kotlin/blob/master/idea/idea-gradle/src/org/jetbrains/kotlin/idea/configuration/KotlinBuildScriptManipulator.kt
 *
 * Created by taojinhou on 2019/1/17.
 */
class BuildGradleKts(project: Project, private val buildFile: PsiFile) : ProjectFile, DependSupport {

    override fun removeDependencies(dependencies: Collection<StarterInfo>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addDependencies(dependencies: Collection<StarterInfo>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addBom(bom: InitializrBom) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addRepositories(repositories: Set<InitializrRepository>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}