package io.github.hdzitao.editstarters.initializr;

import io.github.hdzitao.editstarters.ohub.OHub;
import io.github.hdzitao.editstarters.springboot.SpringBoot;

/**
 * Initializr返回
 *
 * @version 3.2.0
 */
public class InitializrResponse {
    private SpringBoot springBoot;

    private boolean enableCache = false;
    private long cacheUpdateTime;

    private boolean enableOHub = false;
    private OHub oHub;

    public SpringBoot getSpringBoot() {
        return springBoot;
    }

    public void setSpringBoot(SpringBoot springBoot) {
        this.springBoot = springBoot;
    }

    public boolean isEnableCache() {
        return enableCache;
    }

    public void setEnableCache(boolean enableCache) {
        this.enableCache = enableCache;
    }

    public long getCacheUpdateTime() {
        return cacheUpdateTime;
    }

    public void setCacheUpdateTime(long cacheUpdateTime) {
        this.cacheUpdateTime = cacheUpdateTime;
    }

    public boolean isEnableOHub() {
        return enableOHub;
    }

    public void setEnableOHub(boolean enableOHub) {
        this.enableOHub = enableOHub;
    }

    public OHub getOHub() {
        return oHub;
    }

    public void setOHub(OHub oHub) {
        this.oHub = oHub;
    }
}
