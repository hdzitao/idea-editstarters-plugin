package io.github.hdzitao.editstarters.buildsystem.gradle;

import com.intellij.psi.PsiElement;
import io.github.hdzitao.editstarters.buildsystem.DependencyScope;
import io.github.hdzitao.editstarters.buildsystem.ProjectFile;
import io.github.hdzitao.editstarters.dependency.Bom;
import io.github.hdzitao.editstarters.dependency.Point;
import io.github.hdzitao.editstarters.dependency.Repository;
import io.github.hdzitao.editstarters.dependency.Starter;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * build.gradle抽象类
 *
 * @version 3.2.0
 */
//@SuppressWarnings("ConstantConditions")
public abstract class AbstractBuildGradle<T extends PsiElement> extends ProjectFile<T> {
    @Getter
    @Setter
    public static class Instruction {
        private final String inst;
        private final String point;

        public Instruction(String inst, String point) {
            this.inst = inst;
            this.point = point;
        }

        public String toInstString(String format) {
            return format.replace("$inst", inst).replace("$point", point);
        }
    }

    @Getter
    @Setter
    protected static class GradlePoint implements Point {
        private final String groupId;
        private final String artifactId;

        private GradlePoint(String groupId, String artifactId) {
            this.groupId = groupId;
            this.artifactId = artifactId;
        }

        public static GradlePoint of(String groupId, String artifactId) {
            return new GradlePoint(groupId, artifactId);
        }

        public static GradlePoint empty() {
            return new GradlePoint("", "");
        }

        @Override
        public String point() {
            return this.groupId + ":" + this.artifactId;
        }
    }

    public static final String TAG_DEPENDENCY_MANAGEMENT = "dependencies";
    public static final String TAG_BOM_MANAGEMENT = "dependencyManagement";
    public static final String TAG_BOM_IMPORT = "imports";
    public static final String TAG_BOM = "mavenBom";
    public static final String TAG_REPOSITORY_MANAGEMENT = "repositories";
    public static final String TAG_REPOSITORY = "maven";


    protected List<Instruction> dependencyInstruction(Starter info) {
        List<Instruction> instructions = new ArrayList<>();
        String point = info.getGroupId() + ":" + info.getArtifactId()
                + (StringUtils.isNoneBlank(info.getVersion()) ? ":" + info.getVersion() : "");
        for (String inst : resolveScope(DependencyScope.getByScope(info.getScope()))) {
            instructions.add(new Instruction(inst, point));
        }
        return instructions;
    }

    protected Instruction bomInstruction(Bom bom) {
        return new Instruction(TAG_BOM, String.join(":", bom.getGroupId(), bom.getArtifactId(), bom.getVersion()));
    }

    protected Instruction repositoryInstruction(Repository repository) {
        return new Instruction(TAG_REPOSITORY, repository.getUrl());
    }

    protected GradlePoint splitGroupArtifact(String point) {
        if (StringUtils.isNoneBlank(point)) {
            Matcher matcher = Pattern.compile("^([^:]+):([^:]+)").matcher(point);
            if (matcher.find()) {
                return GradlePoint.of(matcher.group(1), matcher.group(2));
            }
        }
        return GradlePoint.empty();
    }

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
}