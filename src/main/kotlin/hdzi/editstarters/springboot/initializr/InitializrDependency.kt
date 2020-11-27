package hdzi.editstarters.springboot.initializr

import hdzi.editstarters.springboot.Dependency

class InitializrDependency(
    override var groupId: String,
    override var artifactId: String,
    var scope: String,
    var version: String?,
    var repository: String?,
    var bom: String?
) : Dependency