package com.github.hdzitao.editstarters.initializr;

import com.github.hdzitao.editstarters.dependency.SpringBoot;

public class CacheInitializr implements Initializr {
    @Override
    public SpringBoot initialize(InitializrParameters parameters, InitializrStatus status, InitializrChain chain) {
        String url = parameters.getUrl();
        String version = parameters.getVersion().getOriginalText();
        CachePersistentComponent cachePersistentComponent = CachePersistentComponent.getInstance(parameters.getProject());

        if (parameters.isEnableCache()) {
            // 如果启用缓存,检查缓存
            SpringBoot springBoot = cachePersistentComponent.get(url, version);
            if (springBoot != null) {
                status.setEnableCache(true);
                status.setCacheUpdateTime(cachePersistentComponent.getUpdateTime());

                return springBoot;
            }
        }
        // 不启用缓存,或缓存为空,继续执行
        SpringBoot springBoot = chain.initialize(parameters, status);
        // 执行完后,不管起不起用缓存都保存最新的结果
        cachePersistentComponent.put(url, version, springBoot);
        return springBoot;
    }
}
