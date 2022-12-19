package hdzi.editstarters.initializr;

import com.github.hdzitao.editstarters.initializr.InitializrMetadataConfig;
import com.google.gson.Gson;
import com.intellij.util.io.HttpRequests;
import org.junit.Test;

import java.io.IOException;

public class InitializrMetadataConfigTest {
    @Test
    public void toBean() throws IOException {
        String url = "https://raw.githubusercontent.com/hdzitao/idea-editstarters-plugin-configure/config/bootVersion/start.spring.io/2.2.2/config.json";
        InitializrMetadataConfig metadataConfig = HttpRequests.request(url).connect(request -> new Gson().fromJson(request.readString(), InitializrMetadataConfig.class));
        System.out.println();
    }
}