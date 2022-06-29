package hdzi.editstarters.dependency;

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
        if (repository != null && !Points.contains(this.repositories, repository)) {
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
