package hdzi.editstarters.buildsystem.maven

import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.psi.xml.XmlFile
import hdzi.editstarters.buildsystem.BuildSystem
import hdzi.editstarters.buildsystem.ProjectDependency
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil

/**
 * Created by taojinhou on 2019/1/11.
 */

class MavenBuildSystem(context: DataContext) : BuildSystem(
    context,
    {
        PomXml(context.getData(CommonDataKeys.PSI_FILE) as XmlFile)
    },
    {
        MavenActionUtil.getMavenProject(context)!!.dependencies
            .map { ProjectDependency(it.groupId, it.artifactId, it.version) }
    })