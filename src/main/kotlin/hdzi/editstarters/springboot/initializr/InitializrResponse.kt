package hdzi.editstarters.springboot.initializr

class InitializrResponse {
    lateinit var dependencies: Map<String, InitializrDependency>
    var repositories: Map<String, InitializrRepository>? = null
    var boms: Map<String, InitializrBom>? = null
}