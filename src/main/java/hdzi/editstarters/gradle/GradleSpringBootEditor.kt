package hdzi.editstarters.gradle

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.DataKeys
import hdzi.editstarters.springboot.SpringBootEditor
import hdzi.editstarters.springboot.bean.ProjectDependency
import hdzi.editstarters.springboot.bean.StarterInfo
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.model.idea.IdeaProject
import org.gradle.tooling.model.idea.IdeaSingleEntryLibraryDependency
import java.io.File

/**
 * Created by taojinhou on 2019/1/14.
 */
class GradleSpringBootEditor(context: DataContext) : SpringBootEditor(context, {
    val basePath = context.getData(DataKeys.PROJECT)?.basePath
    val connect = GradleConnector.newConnector().forProjectDirectory(File(basePath)).connect()
    val ideaModule = connect.getModel(IdeaProject::class.java).modules.getAt(0)
    ideaModule.dependencies.map {
        val moduleVersion = (it as IdeaSingleEntryLibraryDependency).gradleModuleVersion!!
        ProjectDependency(moduleVersion.group, moduleVersion.name, moduleVersion.version)
    }
}) {

    override fun addDependencies(starterInfos: Collection<StarterInfo>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeDependencies(starterInfos: Collection<StarterInfo>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
