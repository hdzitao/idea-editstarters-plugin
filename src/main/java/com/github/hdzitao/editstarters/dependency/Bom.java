package com.github.hdzitao.editstarters.dependency;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Bom implements Point {
    protected String groupId;
    protected String artifactId;
    protected String version;

    public Bom(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public Bom(String groupId, String artifactId) {
        this(groupId, artifactId, null);
    }

    @Override
    public String point() {
        return this.groupId + ":" + this.artifactId;
    }
}