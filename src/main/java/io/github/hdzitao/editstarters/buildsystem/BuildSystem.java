package io.github.hdzitao.editstarters.buildsystem;

import com.intellij.openapi.actionSystem.DataContext;
import io.github.hdzitao.editstarters.dependency.Dependency;
import io.github.hdzitao.editstarters.dependency.Points;
import io.github.hdzitao.editstarters.springboot.EditStarters;
import io.github.hdzitao.editstarters.springboot.Starter;
import lombok.Getter;

import java.util.Collection;
import java.util.List;

/**
 * 构建系统
 *
 * @version 3.2.0
 */
@Getter
public abstract class BuildSystem implements EditStarters {
    private final DataContext context;
    private final ProjectFile<?> projectFile;
    private final List<Dependency> dependencies;
    private final Dependency springbootDependency;
    private final boolean springBootProject;

    public BuildSystem(DataContext context, ProjectFile<?> projectFile, List<Dependency> dependencies) {
        this.context = context;
        this.projectFile = projectFile;
        this.dependencies = dependencies;
        this.springbootDependency = Points.find(this.dependencies, new Dependency("org.springframework.boot", "spring-boot"));
        this.springBootProject = this.springbootDependency != null;
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
