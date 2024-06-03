package io.github.hdzitao.editstarters.springboot;

import java.util.List;

/**
 * 初始化最终结果-分类模块
 *
 * @version 3.2.0
 */
public class Module {
    private String name;
    private List<Starter> values;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Starter> getValues() {
        return values;
    }

    public void setValues(List<Starter> values) {
        this.values = values;
    }
}
