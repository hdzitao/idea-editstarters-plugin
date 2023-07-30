package io.github.hdzitao.editstarters.initializr;

import io.github.hdzitao.editstarters.version.Version;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 初始化最终结果
 *
 * @version 3.2.0
 */
@Getter
@Setter
public class SpringBoot {
    private final Version version;
    private final List<Module> modules;


    public SpringBoot(Version version, List<Module> modules) {
        this.version = version;
        this.modules = modules;
    }
}
