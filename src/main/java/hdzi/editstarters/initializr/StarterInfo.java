package hdzi.editstarters.initializr;

import hdzi.editstarters.dependency.Dependency;
import hdzi.editstarters.dependency.DependencyScope;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by taojinhou on 2018/12/21.
 */
@Data
public class StarterInfo implements Dependency {
    private String id;
    private String name;

    private String description;
    private String versionRange;

    // 坐标信息
    private String groupId;
    private String artifactId;
    private DependencyScope scope;
    private String version;
    private final List<InitializrRepository> repositories = new ArrayList<>();
    private InitializrBom bom;
    private boolean exist = false;

    public void addRepository(InitializrRepository repository) {
        if (repository != null) {
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
