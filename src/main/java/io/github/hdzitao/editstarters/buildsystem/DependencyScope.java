package io.github.hdzitao.editstarters.buildsystem;

/**
 * 依赖的 scope
 * 这个scope是抽象的,具体到maven和gradle是要转化的
 *
 * @version 3.2.0
 */
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

    private final String scope;

    DependencyScope(String scope) {
        this.scope = scope;
    }

    public String getScope() {
        return scope;
    }

    public static DependencyScope getByScope(String scope) {
        for (DependencyScope dependencyScope : values()) {
            if (dependencyScope.scope.equals(scope)) {
                return dependencyScope;
            }
        }

        return COMPILE;
    }

    @Override
    public String toString() {
        return scope;
    }
}