package io.github.hdzitao.editstarters.initializr;

import io.github.hdzitao.editstarters.ohub.OHub;
import io.github.hdzitao.editstarters.springboot.SpringBoot;
import lombok.Getter;
import lombok.Setter;

/**
 * Initializr返回
 *
 * @version 3.2.0
 */
@Getter
@Setter
public class InitializrReturn {
    private final InitializrParameter parameter;
    private SpringBoot springBoot;

    private boolean enableCache = false;
    private long cacheUpdateTime;

    private boolean enableOHub = false;
    private OHub oHub;

    public InitializrReturn(InitializrParameter parameter) {
        this.parameter = parameter;
    }
}
