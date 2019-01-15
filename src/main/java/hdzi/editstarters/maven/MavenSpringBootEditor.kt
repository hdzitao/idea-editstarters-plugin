package hdzi.editstarters.maven

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.psi.xml.XmlFile
import hdzi.editstarters.springboot.SpringBootEditor
import hdzi.editstarters.springboot.bean.Dependency
import hdzi.editstarters.springboot.bean.StarterInfo
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil

/**
 * Created by taojinhou on 2019/1/11.
 */

class MavenSpringBootEditor(context: DataContext) :
    SpringBootEditor(context, {
        MavenActionUtil.getMavenProject(context)!!.dependencies
            .map { Dependency(it.groupId, it.artifactId, it.version) }
    }) {
    /**
     * 自定义的pom文件操作类
     */
    private val pomXml = PomXml(context.getData(DataKeys.PSI_FILE) as XmlFile)

    override fun addDependencies(starterInfos: Collection<StarterInfo>) {
        this.pomXml.addDependencies(starterInfos)
    }

    override fun removeDependencies(starterInfos: Collection<StarterInfo>) {
        this.pomXml.removeDependencies(starterInfos)
    }
}