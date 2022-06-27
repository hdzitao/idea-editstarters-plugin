package hdzi.editstarters.initializr;

import com.google.gson.annotations.SerializedName;
import hdzi.editstarters.dependency.Link;
import hdzi.editstarters.dependency.Module;
import hdzi.editstarters.dependency.StarterInfo;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

@Data
public class InitializrMetadata {
    @SerializedName("_link")
    private MetadataLink link;

    private MetadataDependencies dependencies;


    //==================================================================================================================

    @Data
    public static class MetadataLink {
        private Link dependencies;
    }

    @Data
    public static class MetadataDependencies implements Iterable<StarterInfo> {
        private String type;
        private List<Module> values;

        @NotNull
        @Override
        public Iterator<StarterInfo> iterator() {
            return this.values.stream().flatMap(it -> it.getValues().stream()).iterator();
        }
    }
}
