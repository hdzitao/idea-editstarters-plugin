package hdzi.editstarters.dependency;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by taojinhou on 2018/12/21.
 */
@Data
public class SpringBoot {
    private final String bootVersion;
    private final Map<String, List<StarterInfo>> modules;


    public SpringBoot(String bootVersion, Map<String, List<StarterInfo>> modules) {
        this.bootVersion = bootVersion;
        this.modules = modules;
    }
}
