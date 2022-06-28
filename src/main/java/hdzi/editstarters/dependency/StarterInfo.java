package hdzi.editstarters.dependency;

import com.intellij.util.containers.ContainerUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class StarterInfo extends Dependency {
    private String id;
    private String name;

    private String description;
    private String versionRange;

    private final List<Repository> repositories = new ArrayList<>();
    private Bom bom;

    public void addRepository(String id, Repository repository) {
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
        return Objects.equals(point(), that.point());
    }

    @Override
    public int hashCode() {
        return Objects.hash(point());
    }

    @Override
    public String toString() {
        return this.name;
    }
}
