package hdzi.editstarters.initializr;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitializrStatus {
    // 原始参数
    private final InitializrParameters parameters;

    public InitializrStatus(InitializrParameters parameters) {
        this.parameters = parameters;
    }

    // 缓存状态
    private boolean enableCache = false;
    private long cacheUpdateTime;

    // OthersHub状态
    private boolean enableOHub = false;
    private String oHubName;
}
