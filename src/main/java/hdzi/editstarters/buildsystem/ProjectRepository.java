package hdzi.editstarters.buildsystem;

import hdzi.editstarters.dependency.Repository;
import lombok.Data;

@Data
public class ProjectRepository implements Repository {
    private final String url;

    public ProjectRepository(String url) {
        this.url = url;
    }
}