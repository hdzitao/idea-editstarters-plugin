package io.github.hdzitao.editstarters.springboot;

import io.github.hdzitao.editstarters.version.Version;

import java.util.List;

/**
 * 初始化最终结果
 *
 * @version 3.2.0
 */
public class SpringBoot {
    private final Version version;
    private final List<Module> modules;

    public SpringBoot(Version version, List<Module> modules) {
        this.version = version;
        this.modules = modules;
    }

    public Version getVersion() {
        return version;
    }

    public List<Module> getModules() {
        return modules;
    }
}
