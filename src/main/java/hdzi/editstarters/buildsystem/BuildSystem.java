package hdzi.editstarters.buildsystem;

import com.intellij.openapi.actionSystem.DataContext;
import hdzi.editstarters.EditStarters;
import hdzi.editstarters.dependency.Dependency;
import hdzi.editstarters.dependency.Points;
import hdzi.editstarters.dependency.StarterInfo;
import lombok.Getter;

import java.util.Collection;
import java.util.List;

/**
 * 构建工具父类
 */
public abstract class BuildSystem implements EditStarters {
    @Getter
    private final DataContext context;

    private final EditStarters editStarters;

    @Getter
    private final List<Dependency> dependencies;

    @Getter
    private final Dependency springbootDependency;

    @Getter
    private final boolean springBootProject;

    protected BuildSystem(DataContext context, List<Dependency> dependencies, EditStarters editStarters) {
        this.context = context;
        this.editStarters = editStarters;
        this.dependencies = dependencies;
        this.springbootDependency = Points.find(this.dependencies, new Dependency("org.springframework.boot", "spring-boot"));
        this.springBootProject = this.springbootDependency != null;
    }

    @Override
    public void removeStarters(Collection<StarterInfo> dependencies) {
        this.editStarters.removeStarters(dependencies);
    }

    @Override
    public void addStarters(Collection<StarterInfo> dependencies) {
        this.editStarters.addStarters(dependencies);
    }
}
