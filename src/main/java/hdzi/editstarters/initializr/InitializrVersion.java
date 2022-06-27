package hdzi.editstarters.initializr;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InitializrVersion {
    @Getter
    @Setter
    public static class Value {
        private String id;
        private String name;
    }

    @SerializedName("default")
    private String _default;
    private List<Value> values;
}

