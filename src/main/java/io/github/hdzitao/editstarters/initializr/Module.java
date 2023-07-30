package io.github.hdzitao.editstarters.initializr;

import io.github.hdzitao.editstarters.dependency.Starter;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 初始化最终结果-分类模块
 *
 * @version 3.2.0
 */
@Getter
@Setter
public class Module {
    private String name;
    private List<Starter> values;
}
