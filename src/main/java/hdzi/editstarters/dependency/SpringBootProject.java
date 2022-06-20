package hdzi.editstarters.dependency;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by taojinhou on 2018/12/21.
 */
@Data
public class SpringBootProject {
    private final String bootVersion;
    private final Map<String, List<StarterInfo>> modules;
    private final List<StarterInfo> existStarters;


    public SpringBootProject(String bootVersion, Map<String, List<StarterInfo>> modules) {
        this.bootVersion = bootVersion;
        this.modules = modules;
        this.existStarters = modules.values().stream()
                .flatMap(List::stream)
                .filter(StarterInfo::isExist)
                .collect(Collectors.toList());
    }
}
