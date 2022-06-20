package hdzi.editstarters.initializr;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class InitializrVersion {
    @Data
    public static class Value {
        private String id;
        private String name;
    }

    @SerializedName("default")
    private String _default;
    private List<Value> values;
}

