package io.github.hdzitao.editstarters.initializr;


import io.github.hdzitao.editstarters.cache.InitializrCache;
import io.github.hdzitao.editstarters.springboot.SpringBoot;

public class CacheInitializr implements Initializr {
    @Override
    public void initialize(InitializrParameter parameter, InitializrReturn ret, InitializrChain chain) {
        String url = parameter.getUrl();
        String version = parameter.getVersion().getOriginalText();
        InitializrCache initializrCache = InitializrCache.getInstance(parameter.getProject());

        if (parameter.isEnableCache()) {
            // 如果启用缓存,检查缓存
            SpringBoot springBoot = initializrCache.get(url, version);
            if (springBoot != null) {
                ret.setEnableCache(true);
                ret.setCacheUpdateTime(initializrCache.getUpdateTime());
                ret.setSpringBoot(springBoot);
                return;
            }
        }
        // 不启用缓存,或缓存为空,继续执行
        chain.initialize(parameter, ret);
        // 执行完后,缓存最新的结果
        if (ret.getSpringBoot() != null) {
            initializrCache.put(url, version, ret.getSpringBoot());
        }
    }
}
