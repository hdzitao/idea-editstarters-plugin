package hdzi.editstarters.bean.initializr

import hdzi.editstarters.bean.Dependency

class InitializrDependency(
    override var groupId: String?,
    override var artifactId: String?,
    var scope: String?,
    var version: String?,
    var repository: String?,
    var bom: String?
) : Dependency