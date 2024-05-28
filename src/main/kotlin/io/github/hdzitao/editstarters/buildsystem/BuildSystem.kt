package io.github.hdzitao.editstarters.buildsystem

import io.github.hdzitao.editstarters.dependency.Dependency
import io.github.hdzitao.editstarters.dependency.findPont
import io.github.hdzitao.editstarters.springboot.EditStarters
import io.github.hdzitao.editstarters.springboot.Starter

/**
 * 构建系统
 *
 * @version 3.2.0
 */
abstract class BuildSystem(val projectFile: ProjectFile<*, *>, val dependencies: List<Dependency>) : EditStarters {
    val springbootDependency: Dependency? =
        this.dependencies.findPont(Dependency("org.springframework.boot", "spring-boot"))

    val springBootProject = springbootDependency != null

    override fun addStarters(dependencies: Collection<Starter>) {
        projectFile.addStarters(dependencies)
    }

    override fun removeStarters(dependencies: Collection<Starter>) {
        projectFile.removeStarters(dependencies)
    }
}
