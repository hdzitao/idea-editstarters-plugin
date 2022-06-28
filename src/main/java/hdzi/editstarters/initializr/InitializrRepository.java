package hdzi.editstarters.initializr;

import hdzi.editstarters.dependency.IRepository;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitializrRepository implements IRepository {
    private String id;
    private String name;
    private String url;
    private boolean snapshotEnabled = false;
}