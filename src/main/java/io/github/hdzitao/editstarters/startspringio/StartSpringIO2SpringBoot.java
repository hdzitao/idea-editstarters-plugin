package io.github.hdzitao.editstarters.startspringio;

import io.github.hdzitao.editstarters.dependency.Starter;
import io.github.hdzitao.editstarters.springboot.Module;
import io.github.hdzitao.editstarters.springboot.SpringBoot;
import io.github.hdzitao.editstarters.springboot.SpringBootBuilder;
import io.github.hdzitao.editstarters.startspringio.metadata.*;
import io.github.hdzitao.editstarters.version.Version;
import io.github.hdzitao.editstarters.version.Versions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 从StartSpringIO构建SpringBoot
 *
 * @version 3.2.0
 */
public class StartSpringIO2SpringBoot implements SpringBootBuilder<StartSpringIO> {
    @Override
    public SpringBoot buildSpringBoot(StartSpringIO startSpringIO) {
        Version version = startSpringIO.getVersion();
        List<Module> modules = getDeclaredModules(startSpringIO.getMetadataConfig(), version);

        // 删除无效项
        Iterator<Module> moduleIterator = modules.iterator();
        while (moduleIterator.hasNext()) {
            Module module = moduleIterator.next();
            List<Starter> infos = module.getValues();
            Iterator<Starter> infoIterator = infos.iterator();
            while (infoIterator.hasNext()) {
                Starter starter = infoIterator.next();
                if (StringUtils.isBlank(starter.getGroupId())) {
                    // 检查坐标
                    infoIterator.remove();
                } else if ((StringUtils.isNoneBlank(starter.getVersionRange())
                        && !Versions.parseRange(starter.getVersionRange()).match(version))) {
                    // 版本范围检查
                    infoIterator.remove();
                }
            }
            // 删空了,就删除module
            if (infos.isEmpty()) {
                moduleIterator.remove();
            }
        }

        return new SpringBoot(version, modules);
    }

    private List<Module> getDeclaredModules(MetadataConfig metadataConfig, Version version) {
        Configuration configuration = metadataConfig.getConfiguration();
        Dependencies dependencies = metadataConfig.getDependencies();
        Env env = configuration.getEnv();

        List<Module> modules = new ArrayList<>();
        for (DependenciesContent dependenciesContent : dependencies.getContent()) {
            Module module = new Module();
            modules.add(module);

            module.setName(dependenciesContent.getName());
            module.setValues(new ArrayList<>());
            List<MetadataDependency> dependencyContent = dependenciesContent.getContent();
            for (MetadataDependency content : dependencyContent) {
                Starter starter = new Starter();
                module.getValues().add(starter);

                content = content.resolve(version);

                starter.setId(content.getId());
                starter.setName(content.getName());
                starter.setDescription(content.getDescription());
                starter.setVersionRange(content.getCompatibilityRange());

                starter.setGroupId(content.getGroupId());
                starter.setArtifactId(content.getArtifactId());
                starter.setVersion(content.getVersion());
                starter.setScope(content.getScope());

                String bomId = content.getBom();
                MetaDataBom bom;
                if (StringUtils.isNoneBlank(bomId) && (bom = env.getBoms().get(bomId)) != null) {
                    bom = bom.resolve(version);

                    starter.setBom(bom);

                    List<String> repositories = bom.getRepositories();
                    if (CollectionUtils.isNotEmpty(repositories)) {
                        for (String rid : repositories) {
                            MetadataRepository repository = env.getRepositories().get(rid);

                            repository = repository.resolve();

                            starter.addRepository(rid, repository);
                        }
                    }
                }

                String repositoryId = content.getRepository();
                if (StringUtils.isNoneBlank(repositoryId)) {
                    MetadataRepository repository = env.getRepositories().get(repositoryId);

                    repository = repository.resolve();

                    starter.addRepository(repositoryId, repository);
                }
            }
        }

        return modules;
    }
}
