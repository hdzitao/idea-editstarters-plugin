package hdzi.editstarters.gradle

import com.intellij.psi.PsiFile
import hdzi.editstarters.springboot.ProjectFile
import hdzi.editstarters.springboot.bean.StarterInfo

/**
 * Created by taojinhou on 2019/1/16.
 */
class BuildGradleKts(val kotlinFile: PsiFile) : ProjectFile {
    override fun removeDependencies(dependencies: Collection<StarterInfo>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addDependencies(dependencies: Collection<StarterInfo>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}