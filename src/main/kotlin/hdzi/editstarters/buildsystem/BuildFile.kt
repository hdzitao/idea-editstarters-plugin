package hdzi.editstarters.buildsystem

import com.intellij.psi.PsiElement
import hdzi.editstarters.EditStarters
import hdzi.editstarters.springboot.initializr.InitializrBom
import hdzi.editstarters.springboot.initializr.InitializrRepository
import hdzi.editstarters.springboot.initializr.StarterInfo

abstract class BuildFile<T : PsiElement> : EditStarters {

    override fun removeStarters(dependencies: Collection<StarterInfo>) {
        val dependenciesTag = getOrCreateDependenciesTag()
        // 取已存在的依赖
        val extDependencies = findAllDependencies(dependenciesTag)
        // 转化待删除的依赖成字符串形式，方便对比
        val removeDependencies = dependencies.map { it.point }.toSet()
        // 遍历存在的依赖，如果待删除的依赖包含它，就删除
        for (extDependency in extDependencies) {
            if (removeDependencies.contains(extDependency.point)) {
                extDependency.element?.delete()
            }
        }
    }

    override fun addStarters(dependencies: Collection<StarterInfo>) {
        val dependenciesTag = getOrCreateDependenciesTag()

        dependencies.forEach {
            createDependencyTag(dependenciesTag, it)
            if (it.bom != null) addBom(it.bom!!)
            if (it.repositories.isNotEmpty()) addRepositories(it.repositories)
        }
    }

    /**
     * 添加bom信息
     */
    private fun addBom(bom: InitializrBom) {
        val bomTag = getOrCreateBomsTag()
        // 去重后新建
        findAllBoms(bomTag).find { bom.point == it.point } ?: createBomTag(bomTag, bom)
    }

    /**
     * 添加仓库信息
     */
    private fun addRepositories(repositories: Set<InitializrRepository>) {
        val repositoriesTag = getOrCreateRepositoriesTag()
        val existingRepos = findAllRepositories(repositoriesTag).map { it.point }.toSet()
        repositories.forEach {
            if (!existingRepos.contains(it.point)) createRepositoryTag(repositoriesTag, it)
        }
    }

    protected abstract fun getOrCreateDependenciesTag(): T

    protected abstract fun findAllDependencies(dependenciesTag: T): Sequence<BuildDependency>

    protected abstract fun createDependencyTag(dependenciesTag: T, info: StarterInfo)

    protected abstract fun getOrCreateBomsTag(): T

    protected abstract fun findAllBoms(bomsTag: T): Sequence<BuildBom>

    protected abstract fun createBomTag(bomsTag: T, bom: InitializrBom)

    protected abstract fun getOrCreateRepositoriesTag(): T

    protected abstract fun findAllRepositories(repositoriesTag: T): Sequence<BuildRepository>

    protected abstract fun createRepositoryTag(repositoriesTag: T, repository: InitializrRepository)
}
