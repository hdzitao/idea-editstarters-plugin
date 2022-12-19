package com.github.hdzitao.editstarters.buildsystem;

import com.github.hdzitao.editstarters.EditStarters;
import com.github.hdzitao.editstarters.dependency.*;
import com.github.hdzitao.editstarters.ui.ShowErrorException;
import com.intellij.psi.PsiElement;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class ProjectFile<T extends PsiElement> implements EditStarters {


    @Override
    public void removeStarters(Collection<StarterInfo> dependencies) {
        try {
            if (CollectionUtils.isEmpty(dependencies)) {
                return;
            }

            T dependenciesTag = getOrCreateDependenciesTag();
            // 取已存在的依赖
            List<Dependency> extDependencies = findAllDependencies(dependenciesTag);
            // 遍历存在的依赖，如果待删除的依赖包含它，就删除
            for (Dependency extDependency : extDependencies) {
                if (Points.contains(dependencies, extDependency) && extDependency instanceof DependencyElement) {
                    PsiElement element = ((DependencyElement) extDependency).getElement();
                    if (element != null) {
                        element.delete();
                    }
                }
            }
        } catch (ShowErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new ShowErrorException("Syntax error!", e);
        }

    }

    @Override
    public void addStarters(Collection<StarterInfo> dependencies) {
        try {
            if (CollectionUtils.isEmpty(dependencies)) {
                return;
            }

            T dependenciesTag = getOrCreateDependenciesTag();
            List<Bom> boms = new ArrayList<>();
            List<Repository> repositories = new ArrayList<>();
            for (StarterInfo starterInfo : dependencies) {
                createDependencyTag(dependenciesTag, starterInfo);
                Points.addUniq(boms, starterInfo.getBom());
                Points.addAllUniq(repositories, starterInfo.getRepositories());
            }

            addBoms(boms);
            addRepositories(repositories);
        } catch (ShowErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new ShowErrorException("Syntax error!", e);
        }
    }

    /**
     * 添加bom信息
     */
    private void addBoms(List<Bom> boms) {
        if (CollectionUtils.isEmpty(boms)) {
            return;
        }

        T bomTag = getOrCreateBomsTag();
        // 去重后新建
        List<Bom> allBoms = findAllBoms(bomTag);
        for (Bom bom : boms) {
            if (!Points.contains(allBoms, bom)) {
                createBomTag(bomTag, bom);
            }
        }
    }

    /**
     * 添加仓库信息
     */
    private void addRepositories(Collection<Repository> repositories) {
        if (CollectionUtils.isEmpty(repositories)) {
            return;
        }

        T repositoriesTag = getOrCreateRepositoriesTag();
        List<Repository> allRepos = findAllRepositories(repositoriesTag);
        for (Repository repository : repositories) {
            if (!Points.contains(allRepos, repository)) {
                createRepositoryTag(repositoriesTag, repository);
            }
        }
    }

    protected abstract T getOrCreateDependenciesTag();

    protected abstract List<Dependency> findAllDependencies(T dependenciesTag);

    protected abstract void createDependencyTag(T dependenciesTag, StarterInfo info);

    protected abstract T getOrCreateBomsTag();

    protected abstract List<Bom> findAllBoms(T bomsTag);

    protected abstract void createBomTag(T bomsTag, Bom bom);

    protected abstract T getOrCreateRepositoriesTag();

    protected abstract List<Repository> findAllRepositories(T repositoriesTag);

    protected abstract void createRepositoryTag(T repositoriesTag, Repository repository);
}
