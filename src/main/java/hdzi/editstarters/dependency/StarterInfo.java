package hdzi.editstarters.dependency;

import com.intellij.util.containers.ContainerUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class StarterInfo implements IDependency {
    private String id;
    private String name;

    private String description;
    private String versionRange;

    // 坐标信息
    private String groupId;
    private String artifactId;
    private DependencyScope scope;
    private String version;

    private final List<IRepository> repositories = new ArrayList<>();
    private IBom bom;

    public void addRepository(String id, IRepository repository) {
        if (repository != null && ContainerUtil.find(this.repositories,
                it -> Objects.equals(it.point(), repository.point())) == null) {
            repository.setId(id);
            this.repositories.add(repository);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StarterInfo that = (StarterInfo) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
