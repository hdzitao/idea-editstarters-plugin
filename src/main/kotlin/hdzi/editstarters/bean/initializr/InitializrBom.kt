package hdzi.editstarters.bean.initializr

import hdzi.editstarters.bean.Bom

class InitializrBom : Bom {
    override var groupId: String? = null
    override var artifactId: String? = null
    var version: String? = null
    var repositories: List<String>? = null
}