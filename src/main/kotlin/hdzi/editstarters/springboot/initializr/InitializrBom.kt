package hdzi.editstarters.springboot.initializr

import hdzi.editstarters.springboot.Bom

class InitializrBom(
    override var groupId: String?,
    override var artifactId: String?,
    var version: String?,
    var repositories: List<String>?
) : Bom