package io.github.hdzitao.editstarters.buildsystem;

import com.intellij.psi.PsiElement;
import io.github.hdzitao.editstarters.dependency.Bom;
import io.github.hdzitao.editstarters.dependency.Dependency;
import io.github.hdzitao.editstarters.dependency.Points;
import io.github.hdzitao.editstarters.dependency.Repository;
import io.github.hdzitao.editstarters.springboot.EditStarters;
import io.github.hdzitao.editstarters.springboot.Starter;
import io.github.hdzitao.editstarters.ui.ShowErrorException;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * pom.xml和build.gradle项目文件
 *
 * @version 3.2.0
 */
public abstract class ProjectFile<Psi extends PsiElement> implements EditStarters {
    protected abstract Psi findOrCreateDependenciesTag();

    protected abstract List<Dependency> findAllDependencies(Psi dependenciesTag);

    protected abstract void createDependencyTag(Psi dependenciesTag, Starter info);

    protected abstract Psi findOrCreateBomsTag();

    protected abstract List<Bom> findAllBoms(Psi bomsTag);

    protected abstract void createBomTag(Psi bomsTag, Bom bom);

    protected abstract Psi findOrCreateRepositoriesTag();

    protected abstract List<Repository> findAllRepositories(Psi repositoriesTag);

    protected abstract void createRepositoryTag(Psi repositoriesTag, Repository repository);

    @Override
    public void addStarters(Collection<Starter> dependencies) {
        try {
            if (CollectionUtils.isEmpty(dependencies)) {
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
        } catch (ShowErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new ShowErrorException("Syntax error!", e);
        }
    }

    @Override
    public void removeStarters(Collection<Starter> dependencies) {
        try {
            if (CollectionUtils.isEmpty(dependencies)) {
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
        } catch (ShowErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new ShowErrorException("Syntax error!", e);
        }

    }

    private void addBoms(List<Bom> boms) {
        if (CollectionUtils.isEmpty(boms)) {
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
     * 添加仓库信息
     */
    private void addRepositories(Collection<Repository> repositories) {
        if (CollectionUtils.isEmpty(repositories)) {
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
