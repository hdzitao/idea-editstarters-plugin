package com.github.hdzitao.editstarters.initializr;

import com.github.hdzitao.editstarters.dependency.Bom;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InitializrBom extends Bom {
    private List<String> repositories;
}