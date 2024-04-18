package io.github.hdzitao.editstarters.buildsystem

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import io.github.hdzitao.editstarters.dependency.*
import io.github.hdzitao.editstarters.springboot.EditStarters
import io.github.hdzitao.editstarters.springboot.Starter
import io.github.hdzitao.editstarters.ui.ShowErrorException

/**
 * pom.xml和build.gradle项目文件
 *
 * @version 3.2.0
 */
abstract class ProjectFile<BuildFile : PsiFile, Psi : PsiElement> : EditStarters {

    protected abstract val buildFile: BuildFile

    /**
     * 查找或创建 dependencies
     */
    protected abstract fun BuildFile.findOrCreateDependenciesTag(): Psi

    /**
     * 查找所有 dependency
     */
    protected abstract fun Psi.findAllDependencies(): List<Dependency>

    /**
     * 创建 dependency
     */
    protected abstract fun Psi.createDependencyTag(starter: Starter)

    /**
     * 查找或创建 boms
     */
    protected abstract fun BuildFile.findOrCreateBomsTag(): Psi

    /**
     * 查找所有 bom
     */
    protected abstract fun Psi.findAllBoms(): List<Bom>

    /**
     * 创建 bom
     */
    protected abstract fun Psi.createBomTag(bom: Bom)

    /**
     * 查找或创建 repositories
     */
    protected abstract fun BuildFile.findOrCreateRepositoriesTag(): Psi

    /**
     * 查找所有 repository
     */
    protected abstract fun Psi.findAllRepositories(): List<Repository>

    /**
     * 创建 repository
     */
    protected abstract fun Psi.createRepositoryTag(repository: Repository)

    override fun addStarters(dependencies: Collection<Starter>) {
        try {
            if (dependencies.isEmpty()) {
                return
            }

            val dependenciesTag = buildFile.findOrCreateDependenciesTag()
            val boms = ArrayList<Bom>()
            val repositories = ArrayList<Repository>()
            for (starter in dependencies) {
                dependenciesTag.createDependencyTag(starter)
                boms.addPointUniq(starter.bom)
                repositories.addAllPointsUniq(starter.repositories)
            }

            addBoms(boms)
            addRepositories(repositories)
        } catch (e: ShowErrorException) {
            throw e
        } catch (e: Exception) {
            throw ShowErrorException("Syntax error!", e)
        }
    }

    override fun removeStarters(dependencies: Collection<Starter>) {
        try {
            if (dependencies.isEmpty()) {
                return
            }

            val dependenciesTag = buildFile.findOrCreateDependenciesTag()
            // 取已存在的依赖
            val extDependencies = dependenciesTag.findAllDependencies()
            // 遍历存在的依赖，如果待删除的依赖包含它，就删除
            for (extDependency in extDependencies) {
                if (dependencies.hasPoint(extDependency) && extDependency is DependencyElement<*>) {
                    extDependency.element.delete()
                }
            }
        } catch (e: ShowErrorException) {
            throw e
        } catch (e: Exception) {
            throw ShowErrorException("Syntax error!", e)
        }
    }

    /**
     * 添加 bom
     */
    private fun addBoms(boms: List<Bom>) {
        if (boms.isEmpty()) {
            return
        }

        val bomTag = buildFile.findOrCreateBomsTag()
        // 去重后新建
        val allBoms = bomTag.findAllBoms()
        for (bom in boms) {
            if (!allBoms.hasPoint(bom)) {
                bomTag.createBomTag(bom)
            }
        }
    }

    /**
     * 添加 repositories
     */
    private fun addRepositories(repositories: Collection<Repository>) {
        if (repositories.isEmpty()) {
            return
        }

        val repositoriesTag = buildFile.findOrCreateRepositoriesTag()
        val allRepos = repositoriesTag.findAllRepositories()
        for (repository in repositories) {
            if (!allRepos.hasPoint(repository)) {
                repositoriesTag.createRepositoryTag(repository)
            }
        }
    }

    companion object {
        const val EMPTY: String = ""
    }
}
