package io.github.hdzitao.editstarters.startspringio.metadata;

import com.google.gson.Gson;
import com.intellij.util.io.HttpRequests;
import org.junit.Test;

import java.io.IOException;

public class MetadataConfigTest {

    @Test
    public void init() throws IOException {
        Gson gson = new Gson();
        MetadataConfig metadata = HttpRequests.request("https://start.spring.io/metadata/config").accept("application/json").connect(request ->
                gson.fromJson(request.readString(), MetadataConfig.class));
        System.out.println();
    }

}