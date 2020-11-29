package hdzi.editstarters.springboot.initializr

enum class DependencyScope {
    /**
     * Compile Scope.
     */
    COMPILE,

    /**
     * Compile Only Scope.
     */
    COMPILE_ONLY,

    /**
     * Annotation Processor Scope.
     */
    ANNOTATION_PROCESSOR,

    /**
     * Runtime Scope.
     */
    RUNTIME,

    /**
     * Provided Scope.
     */
    PROVIDED,

    /**
     * Test Scope.
     */
    TEST,
}