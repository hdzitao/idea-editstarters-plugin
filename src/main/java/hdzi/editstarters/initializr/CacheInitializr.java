package hdzi.editstarters.initializr;

import hdzi.editstarters.dependency.SpringBoot;

public class CacheInitializr implements Initializr {
    @Override
    public SpringBoot initialize(InitializrParameters parameters, InitializrChain chain) {
        String url = parameters.getUrl();
        String versionID = parameters.getVersion().toVersionID();
        CachePersistentComponent cachePersistentComponent = CachePersistentComponent.getInstance(parameters.getProject());

        if (parameters.isEnableCache()) {
            // 如果启用缓存,检查缓存
            SpringBoot springBoot = cachePersistentComponent.get(url, versionID);
            if (springBoot != null) {
                return springBoot;
            }
        }
        // 不启用缓存,或缓存为空,继续执行
        SpringBoot springBoot = chain.initialize(parameters);
        // 执行完后,不管起不起用缓存都保存最新的结果
        cachePersistentComponent.put(url, versionID, springBoot);
        return springBoot;
    }
}
