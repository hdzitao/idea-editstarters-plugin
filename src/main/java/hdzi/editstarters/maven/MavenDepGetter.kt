package hdzi.editstarters.maven

import com.intellij.openapi.actionSystem.DataContext
import hdzi.editstarters.springboot.ProjectDependGetter
import hdzi.editstarters.springboot.bean.ProjectDependency
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil

class MavenDepGetter : ProjectDependGetter {
    override fun get(context: DataContext): List<ProjectDependency> =
        MavenActionUtil.getMavenProject(context)!!.dependencies
            .map { ProjectDependency(it.groupId, it.artifactId, it.version) }

}