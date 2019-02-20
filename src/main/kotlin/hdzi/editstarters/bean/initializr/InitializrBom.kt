package hdzi.editstarters.bean.initializr

import hdzi.editstarters.bean.Bom

class InitializrBom(
    override var groupId: String?,
    override var artifactId: String?,
    var version: String?,
    var repositories: List<String>?
) : Bom