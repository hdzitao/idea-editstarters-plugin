package hdzi.editstarters.buildsystem

import com.intellij.openapi.actionSystem.DataContext
import hdzi.editstarters.EditStarters

/**
 * 构建工具父类
 */
abstract class BuildSystem(
    val context: DataContext,
    editStartersGetter: () -> EditStarters,
    dependenciesGetter: () -> List<ProjectDependency>
) : EditStarters by editStartersGetter() {
    val existsDependencyDB: Map<String, ProjectDependency> =
        dependenciesGetter().associateBy({ it.point }, { it })

    val springbootDependency =
        existsDependencyDB[ProjectDependency("org.springframework.boot", "spring-boot").point]

    /**
     * 判断是否是spring boot项目
     */
    val isSpringBootProject: Boolean = springbootDependency != null

}
