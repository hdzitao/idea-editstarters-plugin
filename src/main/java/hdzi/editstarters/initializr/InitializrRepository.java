package hdzi.editstarters.initializr;

import hdzi.editstarters.dependency.Repository;
import lombok.Data;

@Data
public class InitializrRepository implements Repository {
    private String id;
    private String name;
    private String url;
    private boolean snapshotEnabled = false;
}