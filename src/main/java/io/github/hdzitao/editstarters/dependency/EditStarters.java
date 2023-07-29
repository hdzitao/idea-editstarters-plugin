package io.github.hdzitao.editstarters.dependency;


import java.util.List;

/**
 * 插件顶层接口
 *
 * @version 3.2.0
 */
public interface EditStarters {

    /**
     * 添加starters
     */
    void addStarters(List<Starter> dependencies);

    /**
     * 删除starters
     */
    void removeStarters(List<Starter> dependencies);
}
