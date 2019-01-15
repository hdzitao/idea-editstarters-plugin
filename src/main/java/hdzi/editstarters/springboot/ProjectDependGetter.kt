package hdzi.editstarters.springboot

import com.intellij.openapi.actionSystem.DataContext
import hdzi.editstarters.springboot.bean.ProjectDependency

interface ProjectDependGetter {
    operator fun get(context: DataContext): List<ProjectDependency>
}