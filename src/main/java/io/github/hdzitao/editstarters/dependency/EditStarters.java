package io.github.hdzitao.editstarters.dependency;


import java.util.Collection;

/**
 * 插件顶层接口
 *
 * @version 3.2.0
 */
public interface EditStarters {

    /**
     * 添加starters
     */
    void addStarters(Collection<Starter> dependencies);

    /**
     * 删除starters
     */
    void removeStarters(Collection<Starter> dependencies);
}
