package hdzi.editstarters.gradle

import com.intellij.openapi.actionSystem.DataContext
import hdzi.editstarters.springboot.SpringBootEditor
import hdzi.editstarters.springboot.bean.StarterInfo

/**
 * Created by taojinhou on 2019/1/14.
 */
class GradleSpringBootEditor(context: DataContext) : SpringBootEditor(context, GradleDependGetter()) {

    override fun addDependencies(starterInfos: Collection<StarterInfo>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeDependencies(starterInfos: Collection<StarterInfo>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
