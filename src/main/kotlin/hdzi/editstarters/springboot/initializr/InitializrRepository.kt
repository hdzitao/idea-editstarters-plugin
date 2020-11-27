package hdzi.editstarters.springboot.initializr

import hdzi.editstarters.springboot.Repository

class InitializrRepository : Repository {
    lateinit var id: String
    lateinit var name: String
    override lateinit var url: String
    var snapshotEnabled: Boolean = false
}