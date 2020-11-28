package hdzi.editstarters.springboot.initializr

class InitializrResponse {
    lateinit var dependencies: Map<String, InitializrDependency>
    lateinit var repositories: Map<String, InitializrRepository>
    lateinit var boms: Map<String, InitializrBom>
}