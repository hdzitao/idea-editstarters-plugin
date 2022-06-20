package hdzi.editstarters.buildsystem;

import com.intellij.openapi.actionSystem.DataContext;
import hdzi.editstarters.EditStarters;
import hdzi.editstarters.dependency.Point;
import hdzi.editstarters.dependency.StarterInfo;
import lombok.Getter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 构建工具父类
 */
public abstract class BuildSystem implements EditStarters {
    @Getter
    private final DataContext context;

    private final EditStarters editStarters;

    @Getter
    private final Map<String, ProjectDependency> existsDependencyDB;

    @Getter
    private final ProjectDependency springbootDependency;

    @Getter
    private final boolean springBootProject;

    protected BuildSystem(DataContext context, List<ProjectDependency> dependencies, EditStarters editStarters) {
        this.context = context;
        this.editStarters = editStarters;
        this.existsDependencyDB = dependencies.stream().collect(Collectors.toMap(Point::point, d -> d, (o, n) -> n));
        this.springbootDependency = this.existsDependencyDB.get(new ProjectDependency("org.springframework.boot", "spring-boot").point());
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
