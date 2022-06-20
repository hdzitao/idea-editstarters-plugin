package hdzi.editstarters.initializr.chain;

import hdzi.editstarters.dependency.SpringBootProject;
import hdzi.editstarters.initializr.chain.cache.CacheComponent;

public class CacheInitializr implements Initializr {
    @Override
    public SpringBootProject initialize(InitializrParameters parameters, InitializrChain chain) {
        String url = parameters.getUrl();
        String version = parameters.getBuildSystem().getSpringbootDependency().getVersion();
        CacheComponent cacheComponent = null;

        if (parameters.isEnableCache()) {
            // 如果启用缓存,检查缓存
            cacheComponent = CacheComponent.getInstance(parameters.getProject());
            SpringBootProject springBootProject = cacheComponent.get(url, version);
            if (springBootProject != null) {
                return springBootProject;
            }
        }
        // 不启用缓存,或缓存为空,继续执行
        SpringBootProject springBootProject = chain.initialize(parameters);
        // 执行完后,如果启用缓存,保存
        if (cacheComponent != null) {
            cacheComponent.put(url, version, springBootProject);
        }
        return springBootProject;
    }
}
