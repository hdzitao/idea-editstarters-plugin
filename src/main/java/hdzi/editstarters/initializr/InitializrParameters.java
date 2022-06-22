package hdzi.editstarters.initializr;

import com.intellij.openapi.project.Project;
import hdzi.editstarters.buildsystem.BuildSystem;
import lombok.Data;

@Data
public class InitializrParameters {
    private String url;
    private BuildSystem buildSystem;
    private Project project;
    private boolean enableCache = true;
    private OthersHub othersHub;
    private Versions.Version version;
}
