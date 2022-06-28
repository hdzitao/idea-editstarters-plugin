package hdzi.editstarters.initializr;

import com.google.gson.annotations.SerializedName;
import hdzi.editstarters.dependency.Link;
import hdzi.editstarters.dependency.Module;
import hdzi.editstarters.dependency.StarterInfo;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

@Getter
@Setter
public class InitializrMetadataClient {
    @SerializedName("_link")
    private Links link;

    private Dependencies dependencies;


    //==================================================================================================================

    @Getter
    @Setter
    public static class Links {
        private Link dependencies;
    }

    @Getter
    @Setter
    public static class Dependencies implements Iterable<StarterInfo> {
        private String type;
        private List<Module> values;

        @NotNull
        @Override
        public Iterator<StarterInfo> iterator() {
            return this.values.stream().flatMap(it -> it.getValues().stream()).iterator();
        }
    }
}
