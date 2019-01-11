package hdzi.editstarters.maven

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.psi.xml.XmlFile
import hdzi.editstarters.springboot.SpringBootEditor
import hdzi.editstarters.springboot.bean.StarterInfo
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil

/**
 * Created by taojinhou on 2019/1/11.
 */

class MavenSpringBootEditor(context: DataContext) : SpringBootEditor(context) {
    /**
     * maven项目实体
     */
    private val mavenProject = MavenActionUtil.getMavenProject(context)!!
    private val pomXml = PomXml(context.getData(DataKeys.PSI_FILE) as XmlFile)

    override val version: String? = mavenProject.parentId?.version

    override fun isSpringBootProject(): Boolean {
        val parent = mavenProject.parentId

        return parent != null
                && "org.springframework.boot" == parent.groupId
                && "spring-boot-starter-parent" == parent.artifactId
    }

    override fun addExistsStarters() {
        this.mavenProject.dependencies.forEach { mavenDep ->
            this.springInitializr!!.addExistsStarter(mavenDep.groupId, mavenDep.artifactId)
        }
    }

    override fun addDependencies(starterInfos: Collection<StarterInfo>) {
        this.pomXml.addDependencies(starterInfos)
    }

    override fun removeDependencies(starterInfos: Collection<StarterInfo>) {
        this.pomXml.removeDependencies(starterInfos)
    }
}