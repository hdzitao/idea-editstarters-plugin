package hdzi.editstarters.springboot.initializr

import hdzi.editstarters.springboot.Bom

class InitializrBom : Bom {
    override lateinit var groupId: String
    override lateinit var artifactId: String
    lateinit var version: String
    lateinit var repositories: List<String>
}