package hdzi.editstarters.bean.initializr

import hdzi.editstarters.bean.Dependency

class InitializrDependency : Dependency {
    override var groupId: String? = null
    override var artifactId: String? = null
    var scope: String? = null
    var version: String? = null
    var repository: String? = null
    var bom: String? = null
}