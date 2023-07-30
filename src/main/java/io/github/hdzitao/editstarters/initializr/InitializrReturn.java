package io.github.hdzitao.editstarters.initializr;

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

    public InitializrReturn(InitializrParameter parameter) {
        this.parameter = parameter;
    }
}
