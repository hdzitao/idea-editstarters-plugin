package io.github.hdzitao.editstarters.buildsystem

/**
 * 依赖的 scope
 * 这个scope是抽象的,具体到maven和gradle是要转化的
 *
 * @version 3.2.0
 */
enum class DependencyScope(val scope: String) {
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

    override fun toString(): String {
        return scope
    }

    companion object {
        fun getByScope(scope: String?): DependencyScope {
            if (scope != null) {
                for (dependencyScope in entries) {
                    if (dependencyScope.scope == scope) {
                        return dependencyScope
                    }
                }
            }

            return COMPILE
        }
    }
}