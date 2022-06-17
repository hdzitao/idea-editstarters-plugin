package hdzi.editstarters.buildsystem.gradle;

import com.intellij.psi.PsiElement;
import hdzi.editstarters.buildsystem.ProjectFile;
import hdzi.editstarters.dependency.DependencyScope;
import hdzi.editstarters.initializr.InitializrBom;
import hdzi.editstarters.initializr.InitializrRepository;
import hdzi.editstarters.initializr.StarterInfo;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class GradleSyntax<T extends PsiElement> extends ProjectFile<T> {
    @Data
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

    public static final String TAG_DEPENDENCY_MANAGEMENT = "dependencies";
    public static final String TAG_BOM_MANAGEMENT = "dependencyManagement";
    public static final String TAG_BOM_IMPORT = "imports";
    public static final String TAG_BOM = "mavenBom";
    public static final String TAG_REPOSITORY_MANAGEMENT = "repositories";
    public static final String TAG_REPOSITORY = "maven";

    // scope相关指令
    public static final String INS_ANNOTATION_PROCESSOR = "annotationProcessor";
    public static final String INS_COMPILE = "implementation";
    public static final String INS_COMPILE_ONLY = "compileOnly";
    public static final String INS_PROVIDED = "providedRuntime";
    public static final String INS_RUNTIME = "runtimeOnly";
    public static final String INS_TEST = "testImplementation";


    protected List<Instruction> dependencyInstruction(StarterInfo info) {
        String instruction = resolveScope(info.getScope());
        String point = info.getGroupId() + ":" + info.getArtifactId()
                + (info.getVersion() != null ? ":" + info.getVersion() : "");
        List<Instruction> instructions = new ArrayList<>();
        instructions.add(new Instruction(instruction, point));
        // 额外指令
        if (info.getScope() == DependencyScope.ANNOTATION_PROCESSOR) {
            instructions.add(new Instruction(INS_COMPILE_ONLY, point));
        }
        return instructions;
    }

    protected Instruction bomInstruction(InitializrBom bom) {
        return new Instruction(TAG_BOM, String.join(":", bom.getGroupId(), bom.getArtifactId(), bom.getVersion()));
    }

    protected Instruction repositoryInstruction(InitializrRepository repository) {
        return new Instruction(TAG_REPOSITORY, repository.getUrl());
    }

    protected Pair<String, String> splitGroupArtifact(String point) {
        if (StringUtils.isNoneBlank(point)) {
            Matcher matcher = Pattern.compile("^([^:]+):([^:]+)").matcher(point);
            if (matcher.find()) {
                return Pair.of(matcher.group(1), matcher.group(2));
            }
        }
        return Pair.of("", "");
    }

    protected String resolveScope(DependencyScope scope) {
        switch (scope) {
            case ANNOTATION_PROCESSOR:
                return INS_ANNOTATION_PROCESSOR;
            case COMPILE:
                return INS_COMPILE;
            case COMPILE_ONLY:
                return INS_COMPILE_ONLY;
            case PROVIDED:
                return INS_PROVIDED;
            case RUNTIME:
                return INS_RUNTIME;
            case TEST:
                return INS_TEST;
            default:
                return null;
        }
    }

    protected String trimText(String s, char... chars) {
        if (s == null) {
            return s;
        }

        int len = s.length();
        int st = 0;
        char[] val = s.toCharArray();

        while ((st < len) && (ArrayUtils.contains(chars, val[st]))) {
            st++;
        }
        while ((st < len) && (ArrayUtils.contains(chars, val[st]))) {
            len--;
        }
        return ((st > 0) || (len < s.length())) ? s.substring(st, len) : s;
    }
}