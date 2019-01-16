package hdzi.editstarters

import hdzi.editstarters.bean.initializr.InitializrBom
import hdzi.editstarters.bean.initializr.InitializrRepository

interface DependSupport {
    fun addBom(bom: InitializrBom)
    fun addRepositories(repositories: Set<InitializrRepository>)
}