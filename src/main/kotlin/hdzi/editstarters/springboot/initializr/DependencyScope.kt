package hdzi.editstarters.springboot.initializr

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

    override fun toString() = this.scope
}