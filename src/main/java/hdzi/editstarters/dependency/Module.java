package hdzi.editstarters.dependency;

import lombok.Data;

import java.util.List;

@Data
public class Module {
    private String name;
    private List<StarterInfo> values;
}
