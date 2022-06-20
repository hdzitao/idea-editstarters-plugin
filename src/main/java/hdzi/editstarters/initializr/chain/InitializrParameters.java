package hdzi.editstarters.initializr.chain;

import hdzi.editstarters.buildsystem.BuildSystem;
import lombok.Data;

@Data
public class InitializrParameters {
    private String url;
    private BuildSystem buildSystem;
}
