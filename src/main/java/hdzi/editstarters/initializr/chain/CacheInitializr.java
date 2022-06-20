package hdzi.editstarters.initializr.chain;

import hdzi.editstarters.dependency.SpringBoot;
import hdzi.editstarters.initializr.CachePersistentComponent;

public class CacheInitializr implements Initializr {
    @Override
    public SpringBoot initialize(InitializrParameters parameters, InitializrChain chain) {
        String url = parameters.getUrl();
        String version = parameters.getBuildSystem().getSpringbootDependency().getVersion();
        CachePersistentComponent cachePersistentComponent = null;

        if (parameters.isEnableCache()) {
            // 如果启用缓存,检查缓存
            cachePersistentComponent = CachePersistentComponent.getInstance(parameters.getProject());
            SpringBoot springBoot = cachePersistentComponent.get(url, version);
            if (springBoot != null) {
                return springBoot;
            }
        }
        // 不启用缓存,或缓存为空,继续执行
        SpringBoot springBoot = chain.initialize(parameters);
        // 执行完后,如果启用缓存,保存
        if (cachePersistentComponent != null) {
            cachePersistentComponent.put(url, version, springBoot);
        }
        return springBoot;
    }
}
