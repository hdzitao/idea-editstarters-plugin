package io.github.hdzitao.editstarters.buildsystem;

import io.github.hdzitao.editstarters.dependency.Dependency;
import io.github.hdzitao.editstarters.dependency.Points;
import io.github.hdzitao.editstarters.springboot.EditStarters;
import io.github.hdzitao.editstarters.springboot.Starter;

import java.util.Collection;
import java.util.List;

/**
 * 构建系统
 *
 * @version 3.2.0
 */
public abstract class BuildSystem implements EditStarters {
    private final ProjectFile<?> projectFile;
    private final List<Dependency> dependencies;
    private final Dependency springbootDependency;
    private final boolean springBootProject;

    public BuildSystem(ProjectFile<?> projectFile, List<Dependency> dependencies) {
        this.projectFile = projectFile;
        this.dependencies = dependencies;
        this.springbootDependency = Points.find(this.dependencies,
                new Dependency("org.springframework.boot", "spring-boot"));
        this.springBootProject = this.springbootDependency != null;
    }

    public ProjectFile<?> getProjectFile() {
        return projectFile;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public Dependency getSpringbootDependency() {
        return springbootDependency;
    }

    public boolean isSpringBootProject() {
        return springBootProject;
    }

    @Override
    public void addStarters(Collection<Starter> dependencies) {
        projectFile.addStarters(dependencies);
    }

    @Override
    public void removeStarters(Collection<Starter> dependencies) {
        projectFile.removeStarters(dependencies);
    }
}
