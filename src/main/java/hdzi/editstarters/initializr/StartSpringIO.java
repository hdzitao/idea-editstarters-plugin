package hdzi.editstarters.initializr;

import hdzi.editstarters.dependency.Module;
import hdzi.editstarters.dependency.StarterInfo;
import hdzi.editstarters.version.Version;
import hdzi.editstarters.version.Versions;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Getter
@Setter
public class StartSpringIO {

    private final Version version;

    private InitializrMetadataClient metadataClient;

    private InitializrDependencies dependencies;

    private InitializrMetadataConfig metadataConfig;
    private Mode mode;

    public enum Mode {
        CLIENT,
        CONFIG
    }

    public StartSpringIO(Version version) {
        this.version = version;
    }

    public String spliceMetadataClientLink(String url) {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        String metadataLink = "/metadata/client";

        if (url.endsWith(metadataLink)) {
            return url;
        }

        return url + metadataLink;
    }

    public String spliceMetadataConfigLink(String url) {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        String metadataLink = "/metadata/config";

        if (url.endsWith(metadataLink)) {
            return url;
        }

        return url + metadataLink;
    }

    public String getDependenciesUrl() {
        return this.metadataClient.getLink().getDependencies().getHref()
                .replace("{?bootVersion}", "?bootVersion=" + version.getOriginalText());
    }

    public void setMetadataClient(InitializrMetadataClient metadataClient) {
        this.mode = Mode.CLIENT;
        this.metadataClient = metadataClient;
    }

    public void setDependencies(InitializrDependencies dependencies) {
        this.mode = Mode.CLIENT;
        this.dependencies = dependencies;
    }

    public void setMetadataConfig(InitializrMetadataConfig metadataConfig) {
        this.mode = Mode.CONFIG;
        this.metadataConfig = metadataConfig;
    }

    public List<Module> getDeclaredModules() {
        List<Module> modules = new ArrayList<>();
        switch (this.mode) {
            case CLIENT:
                modules.addAll(this.metadataClient.getModules(this.dependencies));
                break;
            case CONFIG:
                modules.addAll(this.metadataConfig.getModules(this.version));
                break;
        }
        // 删除无效项
        Iterator<Module> moduleIterator = modules.iterator();
        while (moduleIterator.hasNext()) {
            Module module = moduleIterator.next();
            List<StarterInfo> infos = module.getValues();
            Iterator<StarterInfo> infoIterator = infos.iterator();
            while (infoIterator.hasNext()) {
                StarterInfo starterInfo = infoIterator.next();
                if (StringUtils.isBlank(starterInfo.getGroupId())) {
                    // 检查坐标
                    infoIterator.remove();
                } else if ((StringUtils.isNoneBlank(starterInfo.getVersionRange())
                        && !Versions.parseRange(starterInfo.getVersionRange()).match(this.version))) {
                    // 版本范围检查
                    infoIterator.remove();
                }
            }
            // 删空了,就删除module
            if (infos.isEmpty()) {
                moduleIterator.remove();
            }
        }

        return modules;
    }
}
