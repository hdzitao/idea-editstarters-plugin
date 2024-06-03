package io.github.hdzitao.editstarters.springboot;


import io.github.hdzitao.editstarters.dependency.Bom;
import io.github.hdzitao.editstarters.dependency.Dependency;
import io.github.hdzitao.editstarters.dependency.Points;
import io.github.hdzitao.editstarters.dependency.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * spring boot starter
 *
 * @version 3.2.0
 */
public final class Starter extends Dependency {
    private String id;
    private String name;
    private String description;
    private String versionRange;

    private final List<Repository> repositories = new ArrayList<>();
    private Bom bom;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersionRange() {
        return versionRange;
    }

    public void setVersionRange(String versionRange) {
        this.versionRange = versionRange;
    }

    public List<Repository> getRepositories() {
        return repositories;
    }

    public Bom getBom() {
        return bom;
    }

    public void setBom(Bom bom) {
        this.bom = bom;
    }

    public void addRepository(String id, Repository repository) {
        if (repository != null && !Points.contains(repositories, repository)) {
            repository.setId(id);
            repositories.add(repository);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Starter that = (Starter) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
