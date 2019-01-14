package hdzi.editstarters.gradle

import com.intellij.openapi.actionSystem.DataContext
import hdzi.editstarters.springboot.SpringBootEditor
import hdzi.editstarters.springboot.bean.StarterInfo

/**
 * Created by taojinhou on 2019/1/14.
 */
class GradleSpringBootEditor(context: DataContext) : SpringBootEditor(context) {

    override val currentVersion: String?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val isSpringBootProject: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun addExistsStarters() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addDependencies(starterInfos: Collection<StarterInfo>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeDependencies(starterInfos: Collection<StarterInfo>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
