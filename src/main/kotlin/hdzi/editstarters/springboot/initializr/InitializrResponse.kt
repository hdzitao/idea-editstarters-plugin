package hdzi.editstarters.springboot.initializr

class InitializrResponse(
    var dependencies: Map<String, InitializrDependency>,
    var repositories: Map<String, InitializrRepository>?,
    var boms: Map<String, InitializrBom>?
)