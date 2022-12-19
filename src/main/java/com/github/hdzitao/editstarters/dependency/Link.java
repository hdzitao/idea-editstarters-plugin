package com.github.hdzitao.editstarters.dependency;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Link {
    private String rel;
    private String href;
    private boolean templated;
    private String title;
    private String description;
}
