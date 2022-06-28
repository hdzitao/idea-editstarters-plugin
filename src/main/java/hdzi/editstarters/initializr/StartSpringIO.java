package hdzi.editstarters.initializr;

import hdzi.editstarters.dependency.DependencyScope;
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

    private Version version;

    private InitializrMetadata metaData;

    private InitializrDependencies dependencies;

    public String spliceMetadataLink(String url) {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        String metadataLink = "/metadata/client";

        if (url.endsWith(metadataLink)) {
            return url;
        }

        return url + metadataLink;
    }

    public String getDependenciesUrl() {
        return this.metaData.getLink().getDependencies().getHref()
                .replace("{?bootVersion}", "?bootVersion=" + version.getOriginalText());
    }

    public void setMetaData(Version version, InitializrMetadata metaData) {
        this.version = version;
        this.metaData = metaData;
    }

    public void setDependencies(InitializrDependencies dependencies) {
        this.dependencies = dependencies;
        this.dependencies.getRepositories().forEach((id, repository) -> repository.setId(id));
        // 组合 dependencies 和 metaData
        for (StarterInfo starterInfo : this.metaData.getDependencies()) {
            InitializrDependency dependency = this.dependencies.getDependencies().get(starterInfo.getId());
            if (dependency != null) {
                starterInfo.setGroupId(dependency.getGroupId());
                starterInfo.setArtifactId(dependency.getArtifactId());
                starterInfo.setVersion(dependency.getVersion());
                starterInfo.setScope(DependencyScope.getByScope(dependency.getScope()));

                InitializrBom bom = this.dependencies.getBoms().get(dependency.getBom());
                if (bom != null) {
                    starterInfo.setBom(bom);
                    bom.getRepositories().forEach(rid -> starterInfo.addRepository(this.dependencies.getRepositories().get(rid)));
                }
                InitializrRepository repository = this.dependencies.getRepositories().get(dependency.getRepository());
                if (repository != null) {
                    starterInfo.addRepository(repository);
                }
            }
        }
    }

    public List<Module> getDeclaredModules() {
        List<Module> modules = new ArrayList<>(this.metaData.getDependencies().getValues());
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
