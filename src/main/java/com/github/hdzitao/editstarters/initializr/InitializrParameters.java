package com.github.hdzitao.editstarters.initializr;

import com.github.hdzitao.editstarters.buildsystem.BuildSystem;
import com.github.hdzitao.editstarters.version.Version;
import com.intellij.openapi.project.Project;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitializrParameters {
    private String url;
    private BuildSystem buildSystem;
    private Project project;
    private boolean enableCache = true;
    private OthersHub othersHub;
    private Version version;
}
