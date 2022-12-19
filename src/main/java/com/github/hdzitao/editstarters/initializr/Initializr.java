package com.github.hdzitao.editstarters.initializr;

import com.github.hdzitao.editstarters.dependency.SpringBoot;

public interface Initializr {
    SpringBoot initialize(InitializrParameters parameters, InitializrStatus status, InitializrChain chain);
}
