package hdzi.editstarters.gradle

/**
 * Created by taojinhou on 2019/1/16.
 */

class Scope {
    companion object {
        private val scopeConversion = mapOf(
            "compile" to "implementation",
            "test" to "testImplementation",
            "provided" to "compileOnly"
        )

        fun mapScope(scope: String?): String =
            if (scope == null) {
                "implementation"
            } else {
                scopeConversion.getOrDefault(scope, scope)
            }
    }
}