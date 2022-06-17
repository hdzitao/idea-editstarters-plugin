package hdzi.editstarters.buildsystem.maven;

import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import hdzi.editstarters.buildsystem.ProjectBom;
import hdzi.editstarters.buildsystem.ProjectDependency;
import hdzi.editstarters.buildsystem.ProjectFile;
import hdzi.editstarters.buildsystem.ProjectRepository;
import hdzi.editstarters.dependency.DependencyScope;
import hdzi.editstarters.initializr.InitializrBom;
import hdzi.editstarters.initializr.InitializrRepository;
import hdzi.editstarters.initializr.StarterInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by taojinhou on 2018/12/24.
 */
public class PomXml extends ProjectFile<XmlTag> {
    /**
     * 根标签
     */
    private XmlTag rootTag;

    public PomXml(XmlFile file) {
        rootTag = file.getDocument().getRootTag();
    }

    @Override
    public XmlTag getOrCreateDependenciesTag() {
        return getOrCreateXmlTag(this.rootTag, "dependencies");
    }

    @Override
    public List<ProjectDependency> findAllDependencies(XmlTag dependenciesTag) {
        return Arrays.stream(dependenciesTag.findSubTags("dependency"))
                .map(tag -> new ProjectDependency(getTagText(tag, "groupId"), getTagText(tag, "artifactId"), tag))
                .collect(Collectors.toList());
    }

    @Override
    public void createDependencyTag(XmlTag dependenciesTag, StarterInfo info) {
        XmlTag dependency = createSubTag(dependenciesTag, "dependency");
        addSubTagWithTextBody(dependency, "groupId", info.getGroupId());
        addSubTagWithTextBody(dependency, "artifactId", info.getArtifactId());
        DependencyScope scope = info.getScope();
        addSubTagWithTextBody(dependency, "scope", resolveScope(scope));
        if (scope == DependencyScope.ANNOTATION_PROCESSOR || scope == DependencyScope.COMPILE_ONLY) {
            addSubTagWithTextBody(dependency, "optional", "true");
        }
        addSubTagWithTextBody(dependency, "version", info.getVersion());
    }

    @Override
    public XmlTag getOrCreateBomsTag() {
        return getOrCreateXmlTag(getOrCreateXmlTag(this.rootTag, "dependencyManagement"), "dependencies");
    }


    @Override
    public List<ProjectBom> findAllBoms(XmlTag bomsTag) {
        return Arrays.stream(bomsTag.findSubTags("dependency"))
                .map(tag -> new ProjectBom(getTagText(tag, "groupId"), getTagText(tag, "artifactId")))
                .collect(Collectors.toList());
    }

    @Override
    public void createBomTag(XmlTag bomsTag, InitializrBom bom) {
        XmlTag dependencyTag = createSubTag(bomsTag, "dependency");
        addSubTagWithTextBody(dependencyTag, "groupId", bom.getGroupId());
        addSubTagWithTextBody(dependencyTag, "artifactId", bom.getArtifactId());
        addSubTagWithTextBody(dependencyTag, "version", bom.getVersion());
        addSubTagWithTextBody(dependencyTag, "type", "pom");
        addSubTagWithTextBody(dependencyTag, "scope", "import");
    }

    @Override
    public XmlTag getOrCreateRepositoriesTag() {
        return getOrCreateXmlTag(this.rootTag, "repositories");
    }

    @Override
    public List<ProjectRepository> findAllRepositories(XmlTag repositoriesTag) {
        return Arrays.stream(repositoriesTag.findSubTags("repository"))
                .map(tag -> new ProjectRepository(getTagText(tag, "url")))
                .collect(Collectors.toList());
    }

    @Override
    public void createRepositoryTag(XmlTag repositoriesTag, InitializrRepository repository) {
        XmlTag repositoryTag = createSubTag(repositoriesTag, "repository");
        addSubTagWithTextBody(repositoryTag, "id", repository.getId());
        addSubTagWithTextBody(repositoryTag, "name", repository.getName());
        addSubTagWithTextBody(repositoryTag, "url", repository.getUrl());
        if (repository.isSnapshotEnabled()) {
            XmlTag snapshotsTag = createSubTag(repositoryTag, "snapshots");
            addSubTagWithTextBody(snapshotsTag, "enabled", String.valueOf(true));
        }
    }

    /**
     * 获取标签里的值
     */
    private String getTagText(XmlTag xmlTag, String name) {
        return xmlTag.findFirstSubTag(name).getValue().getText();
    }

    /**
     * 获取或者新建标签
     */
    private XmlTag getOrCreateXmlTag(XmlTag xmlTag, String name) {
        XmlTag subTag = xmlTag.findFirstSubTag(name);
        if (subTag == null) {
            subTag = createSubTag(xmlTag, name);
        }
        return subTag;
    }

    /**
     * 添加有内容的标签
     */
    private void addSubTagWithTextBody(XmlTag xmlTag, String key, String value) {
        if (StringUtils.isNoneBlank(key) && StringUtils.isNoneBlank(value)) {
            xmlTag.addSubTag(xmlTag.createChildTag(key, xmlTag.getNamespace(), value, false), false);
        }
    }

    /**
     * 新建标签
     */
    private XmlTag createSubTag(XmlTag xmlTag, String name) {
        return xmlTag.addSubTag(xmlTag.createChildTag(name, xmlTag.getNamespace(), null, false), false);
    }

    private String resolveScope(DependencyScope scope) {
        switch (scope) {
            case PROVIDED:
                return "provided";
            case RUNTIME:
                return "runtime";
            case TEST:
                return "test";
            case ANNOTATION_PROCESSOR:
            case COMPILE:
            case COMPILE_ONLY:
            default:
                return null;

        }
    }
}
