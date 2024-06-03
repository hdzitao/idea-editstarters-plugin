package io.github.hdzitao.editstarters.buildsystem;

import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import io.github.hdzitao.editstarters.dependency.Bom;
import io.github.hdzitao.editstarters.dependency.Dependency;
import io.github.hdzitao.editstarters.dependency.Points;
import io.github.hdzitao.editstarters.dependency.Repository;
import io.github.hdzitao.editstarters.springboot.EditStarters;
import io.github.hdzitao.editstarters.springboot.Starter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * pom.xml和build.gradle项目文件
 *
 * @version 3.2.0
 */
public abstract class ProjectFile<Psi extends PsiElement> implements EditStarters {
    protected final static String EMPTY = "";

    /**
     * 查找或创建 dependencies
     */
    protected abstract Psi findOrCreateDependenciesTag();

    /**
     * 查找所有 dependency
     */
    protected abstract List<Dependency> findAllDependencies(Psi dependenciesTag);

    /**
     * 创建 dependency
     */
    protected abstract void createDependencyTag(Psi dependenciesTag, Starter info);

    /**
     * 查找或创建 boms
     */
    protected abstract Psi findOrCreateBomsTag();

    /**
     * 查找所有 bom
     */
    protected abstract List<Bom> findAllBoms(Psi bomsTag);

    /**
     * 创建 bom
     */
    protected abstract void createBomTag(Psi bomsTag, Bom bom);

    /**
     * 查找或创建 repositories
     */
    protected abstract Psi findOrCreateRepositoriesTag();

    /**
     * 查找所有 repository
     */
    protected abstract List<Repository> findAllRepositories(Psi repositoriesTag);

    /**
     * 创建 repository
     */
    protected abstract void createRepositoryTag(Psi repositoriesTag, Repository repository);

    @Override
    public void addStarters(Collection<Starter> dependencies) {
        if (ContainerUtil.isEmpty(dependencies)) {
            return;
        }

        Psi dependenciesTag = findOrCreateDependenciesTag();
        List<Bom> boms = new ArrayList<>();
        List<Repository> repositories = new ArrayList<>();
        for (Starter starter : dependencies) {
            createDependencyTag(dependenciesTag, starter);
            Points.addUniq(boms, starter.getBom());
            Points.addAllUniq(repositories, starter.getRepositories());
        }

        addBoms(boms);
        addRepositories(repositories);
    }

    @Override
    public void removeStarters(Collection<Starter> dependencies) {
        if (ContainerUtil.isEmpty(dependencies)) {
            return;
        }

        Psi dependenciesTag = findOrCreateDependenciesTag();
        // 取已存在的依赖
        List<Dependency> extDependencies = findAllDependencies(dependenciesTag);
        // 遍历存在的依赖，如果待删除的依赖包含它，就删除
        for (Dependency extDependency : extDependencies) {
            if (Points.contains(dependencies, extDependency) && extDependency instanceof DependencyElement) {
                @SuppressWarnings("unchecked")
                PsiElement element = ((DependencyElement<PsiElement>) extDependency).getElement();
                if (element != null) {
                    element.delete();
                }
            }
        }

    }

    /**
     * 添加 bom
     */
    private void addBoms(List<Bom> boms) {
        if (ContainerUtil.isEmpty(boms)) {
            return;
        }

        Psi bomTag = findOrCreateBomsTag();
        // 去重后新建
        List<Bom> allBoms = findAllBoms(bomTag);
        for (Bom bom : boms) {
            if (!Points.contains(allBoms, bom)) {
                createBomTag(bomTag, bom);
            }
        }
    }

    /**
     * 添加 repositories
     */
    private void addRepositories(Collection<Repository> repositories) {
        if (ContainerUtil.isEmpty(repositories)) {
            return;
        }

        Psi repositoriesTag = findOrCreateRepositoriesTag();
        List<Repository> allRepos = findAllRepositories(repositoriesTag);
        for (Repository repository : repositories) {
            if (!Points.contains(allRepos, repository)) {
                createRepositoryTag(repositoriesTag, repository);
            }
        }
    }
}
