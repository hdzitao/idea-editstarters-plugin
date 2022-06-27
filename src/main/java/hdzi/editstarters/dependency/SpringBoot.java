package hdzi.editstarters.dependency;

import lombok.Data;

import java.util.List;

/**
 * Created by taojinhou on 2018/12/21.
 */
@Data
public class SpringBoot {
    private final String bootVersion;
    private final List<Module> modules;


    public SpringBoot(String bootVersion, List<Module> modules) {
        this.bootVersion = bootVersion;
        this.modules = modules;
    }
}
