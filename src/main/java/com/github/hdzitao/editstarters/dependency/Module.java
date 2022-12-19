package com.github.hdzitao.editstarters.dependency;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Module {
    private String name;
    private List<StarterInfo> values;
}
