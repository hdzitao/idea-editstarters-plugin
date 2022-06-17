package hdzi.editstarters.buildsystem;

import com.intellij.openapi.actionSystem.DataContext;
import hdzi.editstarters.EditStarters;
import hdzi.editstarters.dependency.Point;
import hdzi.editstarters.initializr.StarterInfo;
import lombok.Getter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
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

    /**
     * 判断是否是spring boot项目
     */
    @Getter
    private final boolean springBootProject;

    public BuildSystem(DataContext context,
                       Supplier<EditStarters> editStartersGetter,
                       Supplier<List<ProjectDependency>> dependenciesGetter) {
        this.context = context;
        this.editStarters = editStartersGetter.get();
        this.existsDependencyDB = dependenciesGetter.get().stream().collect(Collectors.toMap(Point::point, d -> d));
        this.springbootDependency = this.existsDependencyDB.get(
                new ProjectDependency("org.springframework.boot", "spring-boot").point());
        this.springBootProject = this.springbootDependency != null;
    }

    @Override
    public void removeStarters(Collection<StarterInfo> dependencies) {
        editStarters.removeStarters(dependencies);
    }

    @Override
    public void addStarters(Collection<StarterInfo> dependencies) {
        editStarters.addStarters(dependencies);
    }
}
