package io.github.hdzitao.editstarters.buildsystem.gradle;

import com.intellij.psi.PsiElement;
import io.github.hdzitao.editstarters.buildsystem.DependencyScope;
import io.github.hdzitao.editstarters.buildsystem.ProjectFile;
import io.github.hdzitao.editstarters.dependency.Bom;
import io.github.hdzitao.editstarters.dependency.Repository;
import io.github.hdzitao.editstarters.springboot.Starter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * build.gradle抽象类
 *
 * @version 3.2.0
 */
//@SuppressWarnings("ConstantConditions")
public abstract class AbstractBuildGradle<T extends PsiElement> extends ProjectFile<T> {

    /**
     * gradle语法简单抽象
     */
    @Getter
    @AllArgsConstructor
    public static class Instruction {
        private final String inst;
        private final String point;

        public String toInstString(String format) {
            return format.replace("${inst}", inst).replace("${point}", point);
        }
    }

    // TAG ==============================================================================
    public static final String TAG_DEPENDENCY_MANAGEMENT = "dependencies";
    public static final String TAG_BOM_MANAGEMENT = "dependencyManagement";
    public static final String TAG_BOM_IMPORT = "imports";
    public static final String TAG_BOM = "mavenBom";
    public static final String TAG_REPOSITORY_MANAGEMENT = "repositories";
    public static final String TAG_REPOSITORY = "maven";
    // TAG ==============================================================================

    protected final static String EMPTY = "";

    /**
     * dependency语法
     *
     * @param info
     * @return
     */
    protected List<Instruction> dependencyInstruction(Starter info) {
        List<Instruction> instructions = new ArrayList<>();
        String point = splicingDependency(info.getGroupId(), info.getArtifactId(), info.getVersion());
        for (String inst : resolveScope(DependencyScope.getByScope(info.getScope()))) {
            instructions.add(new Instruction(inst, point));
        }
        return instructions;
    }

    /**
     * bom语法
     *
     * @param bom
     * @return
     */
    protected Instruction bomInstruction(Bom bom) {
        String point = splicingDependency(bom.getGroupId(), bom.getArtifactId(), bom.getVersion());
        return new Instruction(TAG_BOM, point);
    }

    /**
     * repository语句
     *
     * @param repository
     * @return
     */
    protected Instruction repositoryInstruction(Repository repository) {
        return new Instruction(TAG_REPOSITORY, repository.getUrl());
    }

    /**
     * 割出GroupID/ArtifactID构建Depend
     *
     * @param point
     * @return
     */
    protected <Depend> Depend newByGroupArtifact(String point, BiFunction<String, String, Depend> buildFun) {
        if (StringUtils.isNoneBlank(point)) {
            String[] groupArtifact = point.split(":");
            if (groupArtifact.length >= 2) {
                return buildFun.apply(groupArtifact[0], groupArtifact[1]);
            }
        }
        return buildFun.apply(EMPTY, EMPTY);
    }

    /**
     * 处理scope
     *
     * @param scope
     * @return
     */
    protected String[] resolveScope(DependencyScope scope) {
        switch (scope) {
            case ANNOTATION_PROCESSOR:
                return new String[]{"compileOnly", "annotationProcessor"};
            case COMPILE:
                return new String[]{"implementation"};
            case COMPILE_ONLY:
                return new String[]{"compileOnly"};
            case PROVIDED:
                return new String[]{"providedRuntime"};
            case RUNTIME:
                return new String[]{"runtimeOnly"};
            case TEST:
                return new String[]{"testImplementation"};
            default:
                return null;
        }
    }

    /**
     * " aaa " => "aaa"
     *
     * @param s
     * @param chars
     * @return
     */
    protected String trimText(String s, char... chars) {
        if (s == null) {
            return "";
        }

        int len = s.length();
        int st = 0;
        char[] val = s.toCharArray();

        while ((st < len) && (ArrayUtils.contains(chars, val[st]))) {
            st++;
        }
        while ((st < len) && (ArrayUtils.contains(chars, val[len - 1]))) {
            len--;
        }
        return ((st > 0) || (len < s.length())) ? s.substring(st, len) : s;
    }

    /**
     * 拼接 groupId:artifactId:version
     *
     * @param groupId
     * @param artifactId
     * @param version
     * @return
     */
    protected String splicingDependency(String groupId, String artifactId, String version) {
        groupId = StringUtils.isNoneBlank(groupId) ? groupId : "";
        artifactId = StringUtils.isNoneBlank(artifactId) ? artifactId : "";

        String point = groupId + ":" + artifactId;

        if (StringUtils.isNoneBlank(version)) {
            point = point + ":" + version;
        }

        return point;
    }

}