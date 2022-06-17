package hdzi.editstarters.dependency;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 依赖的 scope
 * 这个scope是抽象的,具体到maven和gradle是要转化的
 */
@AllArgsConstructor
public enum DependencyScope {
    /**
     * Compile Scope.
     */
    COMPILE("compile"),

    /**
     * Compile Only Scope.
     */
    COMPILE_ONLY("compileOnly"),

    /**
     * Annotation Processor Scope.
     */
    ANNOTATION_PROCESSOR("annotationProcessor"),

    /**
     * Runtime Scope.
     */
    RUNTIME("runtime"),

    /**
     * Provided Scope.
     */
    PROVIDED("provided"),

    /**
     * Test Scope.
     */
    TEST("test"),
    ;

    @Getter
    private final String scope;

    @Override
    public String toString() {
        return this.scope;
    }
}