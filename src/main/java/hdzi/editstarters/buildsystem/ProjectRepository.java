package hdzi.editstarters.buildsystem;

import hdzi.editstarters.dependency.Repository;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectRepository implements Repository {
    private final String url;

    public ProjectRepository(String url) {
        this.url = url;
    }
}