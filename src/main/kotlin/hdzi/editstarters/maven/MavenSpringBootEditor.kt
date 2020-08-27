package hdzi.editstarters.maven

import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.psi.xml.XmlFile
import hdzi.editstarters.bean.project.ProjectDependency
import hdzi.editstarters.springboot.SpringBootEditor
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil

/**
 * Created by taojinhou on 2019/1/11.
 */

class MavenSpringBootEditor(context: DataContext) : SpringBootEditor(
    context,
    {
        PomXml(context.getData(CommonDataKeys.PSI_FILE) as XmlFile)
    },
    {
        MavenActionUtil.getMavenProject(context)!!.dependencies
            .map { ProjectDependency(it.groupId, it.artifactId, it.version) }
    })