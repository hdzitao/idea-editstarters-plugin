package hdzi.editstarters.springboot.initializr

import hdzi.editstarters.springboot.Dependency

class InitializrDependency : Dependency {
    override lateinit var groupId: String
    override lateinit var artifactId: String
    lateinit var scope: String
    var version: String? = null
    var repository: String? = null
    var bom: String? = null
}