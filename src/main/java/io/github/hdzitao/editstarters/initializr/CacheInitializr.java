package io.github.hdzitao.editstarters.initializr;


import io.github.hdzitao.editstarters.cache.InitializrCache;
import io.github.hdzitao.editstarters.springboot.SpringBoot;

/**
 * 缓存初始化
 *
 * @version 3.2.0
 */
public class CacheInitializr implements Initializr {
    @Override
    public void initialize(InitializrRequest request, InitializrResponse response, InitializrChain chain) throws Exception {
        String url = request.getUrl();
        String version = request.getVersion().getOriginalText();
        InitializrCache initializrCache = InitializrCache.getInstance(request.getProject());

        if (request.isEnableCache()) {
            // 如果启用缓存,检查缓存
            SpringBoot springBoot = initializrCache.getSpringBoot(url, version);
            if (springBoot != null) {
                response.setEnableCache(true);
                response.setCacheUpdateTime(initializrCache.getUpdateTime());
                response.setSpringBoot(springBoot);
                return;
            }
        }
        // 不启用缓存,或缓存为空,继续执行
        chain.initialize(request, response);
        // 执行完后,缓存最新的结果
        if (response.getSpringBoot() != null) {
            initializrCache.putSpringBoot(url, version, response.getSpringBoot());
        }
    }
}
