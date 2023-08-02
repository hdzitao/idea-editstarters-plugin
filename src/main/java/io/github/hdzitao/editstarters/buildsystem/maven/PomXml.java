package io.github.hdzitao.editstarters.buildsystem.maven;

import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import io.github.hdzitao.editstarters.buildsystem.DependencyElement;
import io.github.hdzitao.editstarters.buildsystem.DependencyScope;
import io.github.hdzitao.editstarters.buildsystem.ProjectFile;
import io.github.hdzitao.editstarters.dependency.Bom;
import io.github.hdzitao.editstarters.dependency.Dependency;
import io.github.hdzitao.editstarters.dependency.Repository;
import io.github.hdzitao.editstarters.springboot.Starter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * pom.xml文件
 *
 * @version 3.2.0
 */
@SuppressWarnings("ConstantConditions")
public class PomXml extends ProjectFile<XmlTag> {
    // TAG ==================================================================================
    private static final String TAG_DEPENDENCY_MANAGEMENT = "dependencyManagement";
    private static final String TAG_DEPENDENCIES = "dependencies";
    private static final String TAG_DEPENDENCY = "dependency";
    private static final String TAG_GROUP_ID = "groupId";
    private static final String TAG_ARTIFACT_ID = "artifactId";
    private static final String TAG_VERSION = "version";
    private static final String TAG_SCOPE = "scope";
    private static final String TAG_REPOSITORIES = "repositories";
    private static final String TAG_REPOSITORY = "repository";
    // TAG ==================================================================================

    /**
     * 根标签
     */
    private final XmlTag rootTag;

    public PomXml(XmlFile file) {
        rootTag = file.getDocument().getRootTag();
    }

    @Override
    public XmlTag findOrCreateDependenciesTag() {
        return getOrCreateXmlTag(rootTag, TAG_DEPENDENCIES);
    }

    @Override
    public List<Dependency> findAllDependencies(XmlTag dependenciesTag) {
        return Arrays.stream(dependenciesTag.findSubTags(TAG_DEPENDENCY))
                .map(tag -> new DependencyElement<>(getTagText(tag, TAG_GROUP_ID), getTagText(tag, TAG_ARTIFACT_ID), tag))
                .collect(Collectors.toList());
    }

    @Override
    public void createDependencyTag(XmlTag dependenciesTag, Starter starter) {
        XmlTag dependency = createSubTag(dependenciesTag, TAG_DEPENDENCY);
        addSubTagWithTextBody(dependency, TAG_GROUP_ID, starter.getGroupId());
        addSubTagWithTextBody(dependency, TAG_ARTIFACT_ID, starter.getArtifactId());
        DependencyScope scope = DependencyScope.getByScope(starter.getScope());
        addSubTagWithTextBody(dependency, TAG_SCOPE, resolveScope(scope));
        if (scope == DependencyScope.ANNOTATION_PROCESSOR || scope == DependencyScope.COMPILE_ONLY) {
            addSubTagWithTextBody(dependency, "optional", "true");
        }
        addSubTagWithTextBody(dependency, TAG_VERSION, starter.getVersion());
    }

    @Override
    public XmlTag findOrCreateBomsTag() {
        return getOrCreateXmlTag(getOrCreateXmlTag(rootTag, TAG_DEPENDENCY_MANAGEMENT), TAG_DEPENDENCIES);
    }


    @Override
    public List<Bom> findAllBoms(XmlTag bomsTag) {
        return Arrays.stream(bomsTag.findSubTags(TAG_DEPENDENCY))
                .map(tag -> new Bom(getTagText(tag, TAG_GROUP_ID), getTagText(tag, TAG_ARTIFACT_ID)))
                .collect(Collectors.toList());
    }

    @Override
    public void createBomTag(XmlTag bomsTag, Bom bom) {
        XmlTag dependencyTag = createSubTag(bomsTag, TAG_DEPENDENCY);
        addSubTagWithTextBody(dependencyTag, TAG_GROUP_ID, bom.getGroupId());
        addSubTagWithTextBody(dependencyTag, TAG_ARTIFACT_ID, bom.getArtifactId());
        addSubTagWithTextBody(dependencyTag, TAG_VERSION, bom.getVersion());
        addSubTagWithTextBody(dependencyTag, "type", "pom");
        addSubTagWithTextBody(dependencyTag, TAG_SCOPE, "import");
    }

    @Override
    public XmlTag findOrCreateRepositoriesTag() {
        return getOrCreateXmlTag(rootTag, TAG_REPOSITORIES);
    }

    @Override
    public List<Repository> findAllRepositories(XmlTag repositoriesTag) {
        return Arrays.stream(repositoriesTag.findSubTags(TAG_REPOSITORY))
                .map(tag -> new Repository(getTagText(tag, "url")))
                .collect(Collectors.toList());
    }

    @Override
    public void createRepositoryTag(XmlTag repositoriesTag, Repository repository) {
        XmlTag repositoryTag = createSubTag(repositoriesTag, TAG_REPOSITORY);
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
        XmlTag subTag = xmlTag.findFirstSubTag(name);
        if (subTag == null) {
            return "";
        }
        return subTag.getValue().getText();
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
