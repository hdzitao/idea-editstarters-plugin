package hdzi.editstarters.bean.initializr

class InitializrResponse {
    var repositories: Map<String, InitializrRepository>? = null
    var boms: Map<String, InitializrBom>? = null
    var dependencies: Map<String, InitializrDependency>? = null
}
