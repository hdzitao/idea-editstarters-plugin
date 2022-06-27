package hdzi.editstarters.dependency;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SpringBoot {
    private final String bootVersion;
    private final List<Module> modules;


    public SpringBoot(String bootVersion, List<Module> modules) {
        this.bootVersion = bootVersion;
        this.modules = modules;
    }
}
