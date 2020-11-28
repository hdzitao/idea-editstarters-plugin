package hdzi.editstarters.buildsystem

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
    ;

    companion object {
        fun get(scope: String): DependencyScope =
            when (scope) {
                "compile" -> COMPILE
                "compileOnly" -> COMPILE_ONLY
                "annotationProcessor" -> ANNOTATION_PROCESSOR
                "runtime" -> RUNTIME
                "provided" -> PROVIDED
                "test" -> TEST
                else -> COMPILE
            }
    }
}