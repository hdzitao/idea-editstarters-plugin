package com.github.hdzitao.editstarters.initializr;

import com.github.hdzitao.editstarters.dependency.SpringBoot;
import com.github.hdzitao.editstarters.ui.ShowErrorException;

public class InitializrChain {
    private static final Initializr[] CHAIN = {
            new CacheInitializr(), // 通过缓存处理
            new SpringInitializr(), // 默认处理
            new OthersInitializr() // 其他版本处理
    };
    private int i = 0;

    public SpringBoot initialize(InitializrParameters parameters, InitializrStatus status) {
        if (i >= CHAIN.length) {
            throw ShowErrorException.internal();
        }
        return CHAIN[i++].initialize(parameters, status, this);
    }
}
