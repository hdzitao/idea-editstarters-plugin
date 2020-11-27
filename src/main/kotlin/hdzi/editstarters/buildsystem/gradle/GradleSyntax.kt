package hdzi.editstarters.buildsystem.gradle

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
