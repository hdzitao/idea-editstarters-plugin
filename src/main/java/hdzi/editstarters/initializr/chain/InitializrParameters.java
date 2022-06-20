package hdzi.editstarters.initializr.chain;

import com.intellij.openapi.project.Project;
import hdzi.editstarters.buildsystem.BuildSystem;
import lombok.Data;

@Data
public class InitializrParameters {
    private String url;
    private BuildSystem buildSystem;
    private Project project;
    private boolean enableCache = true;
}
