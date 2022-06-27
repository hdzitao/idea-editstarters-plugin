package hdzi.editstarters.initializr;

import hdzi.editstarters.dependency.Repository;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitializrRepository implements Repository {
    private String id;
    private String name;
    private String url;
    private boolean snapshotEnabled = false;
}