package com.github.hdzitao.editstarters.initializr;

import com.github.hdzitao.editstarters.dependency.Module;
import com.github.hdzitao.editstarters.dependency.StarterInfo;
import com.github.hdzitao.editstarters.version.Version;
import com.github.hdzitao.editstarters.version.Versions;
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

    private InitializrMetadataConfig metadataConfig;

    public StartSpringIO(Version version) {
        this.version = version;
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

    public void setMetadataConfig(InitializrMetadataConfig metadataConfig) {
        this.metadataConfig = metadataConfig;
    }

    public List<Module> getDeclaredModules() {
        List<Module> modules = new ArrayList<>(this.metadataConfig.getModules(this.version));
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
