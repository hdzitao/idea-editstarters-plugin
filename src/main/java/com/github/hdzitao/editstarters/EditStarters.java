package com.github.hdzitao.editstarters;

import com.github.hdzitao.editstarters.dependency.StarterInfo;

import java.util.Collection;

/**
 * 整个项目的顶级功能
 */
public interface EditStarters {
    /**
     * 删除starters
     */
    void removeStarters(Collection<StarterInfo> dependencies);

    /**
     * 添加starters
     */
    void addStarters(Collection<StarterInfo> dependencies);
}