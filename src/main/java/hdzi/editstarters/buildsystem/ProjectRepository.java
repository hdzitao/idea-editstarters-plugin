package hdzi.editstarters.buildsystem;

import hdzi.editstarters.dependency.IRepository;
import hdzi.editstarters.ui.ShowErrorException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectRepository implements IRepository {
    private final String url;

    public ProjectRepository(String url) {
        this.url = url;
    }

    @Override
    public String getId() {
        throw ShowErrorException.internal();
    }

    @Override
    public void setId(String id) {
        throw ShowErrorException.internal();
    }

    @Override
    public String getName() {
        throw ShowErrorException.internal();
    }

    @Override
    public boolean isSnapshotEnabled() {
        throw ShowErrorException.internal();
    }
}