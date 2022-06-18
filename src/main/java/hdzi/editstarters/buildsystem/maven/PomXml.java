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
@SuppressWarnings("ConstantConditions")
public class PomXml extends ProjectFile<XmlTag> {
    private static final String TAG_DEPENDENCY_MANAGEMENT = "dependencyManagement";
    private static final String TAG_DEPENDENCIES = "dependencies";
    private static final String TAG_DEPENDENCY = "dependency";
    private static final String TAG_GROUP_ID = "groupId";
    private static final String TAG_ARTIFACT_ID = "artifactId";
    private static final String TAG_VERSION = "version";
    private static final String TAG_SCOPE = "scope";
    private static final String TAG_REPOSITORIES = "repositories";
    private static final String TAG_REPOSITORY = "repository";
    /**
     * 根标签
     */
    private final XmlTag rootTag;

    public PomXml(XmlFile file) {
        rootTag = file.getDocument().getRootTag();
    }

    @Override
    public XmlTag getOrCreateDependenciesTag() {
        return getOrCreateXmlTag(this.rootTag, TAG_DEPENDENCIES);
    }

    @Override
    public List<ProjectDependency> findAllDependencies(XmlTag dependenciesTag) {
        return Arrays.stream(dependenciesTag.findSubTags(TAG_DEPENDENCY))
                .map(tag -> new ProjectDependency(getTagText(tag, TAG_GROUP_ID), getTagText(tag, TAG_ARTIFACT_ID), tag))
                .collect(Collectors.toList());
    }

    @Override
    public void createDependencyTag(XmlTag dependenciesTag, StarterInfo info) {
        XmlTag dependency = createSubTag(dependenciesTag, TAG_DEPENDENCY);
        addSubTagWithTextBody(dependency, TAG_GROUP_ID, info.getGroupId());
        addSubTagWithTextBody(dependency, TAG_ARTIFACT_ID, info.getArtifactId());
        DependencyScope scope = info.getScope();
        addSubTagWithTextBody(dependency, TAG_SCOPE, resolveScope(scope));
        if (scope == DependencyScope.ANNOTATION_PROCESSOR || scope == DependencyScope.COMPILE_ONLY) {
            addSubTagWithTextBody(dependency, "optional", "true");
        }
        addSubTagWithTextBody(dependency, TAG_VERSION, info.getVersion());
    }

    @Override
    public XmlTag getOrCreateBomsTag() {
        return getOrCreateXmlTag(getOrCreateXmlTag(this.rootTag, TAG_DEPENDENCY_MANAGEMENT), TAG_DEPENDENCIES);
    }


    @Override
    public List<ProjectBom> findAllBoms(XmlTag bomsTag) {
        return Arrays.stream(bomsTag.findSubTags(TAG_DEPENDENCY))
                .map(tag -> new ProjectBom(getTagText(tag, TAG_GROUP_ID), getTagText(tag, TAG_ARTIFACT_ID)))
                .collect(Collectors.toList());
    }

    @Override
    public void createBomTag(XmlTag bomsTag, InitializrBom bom) {
        XmlTag dependencyTag = createSubTag(bomsTag, TAG_DEPENDENCY);
        addSubTagWithTextBody(dependencyTag, TAG_GROUP_ID, bom.getGroupId());
        addSubTagWithTextBody(dependencyTag, TAG_ARTIFACT_ID, bom.getArtifactId());
        addSubTagWithTextBody(dependencyTag, TAG_VERSION, bom.getVersion());
        addSubTagWithTextBody(dependencyTag, "type", "pom");
        addSubTagWithTextBody(dependencyTag, TAG_SCOPE, "import");
    }

    @Override
    public XmlTag getOrCreateRepositoriesTag() {
        return getOrCreateXmlTag(this.rootTag, TAG_REPOSITORIES);
    }

    @Override
    public List<ProjectRepository> findAllRepositories(XmlTag repositoriesTag) {
        return Arrays.stream(repositoriesTag.findSubTags(TAG_REPOSITORY))
                .map(tag -> new ProjectRepository(getTagText(tag, "url")))
                .collect(Collectors.toList());
    }

    @Override
    public void createRepositoryTag(XmlTag repositoriesTag, InitializrRepository repository) {
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
