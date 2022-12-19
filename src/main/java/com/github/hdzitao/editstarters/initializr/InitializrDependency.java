package com.github.hdzitao.editstarters.initializr;


import com.github.hdzitao.editstarters.dependency.Dependency;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitializrDependency extends Dependency {

    private String repository;
    private String bom;
}