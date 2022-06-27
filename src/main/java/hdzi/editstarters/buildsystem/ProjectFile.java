package hdzi.editstarters.buildsystem;

import com.intellij.psi.PsiElement;
import hdzi.editstarters.EditStarters;
import hdzi.editstarters.dependency.Bom;
import hdzi.editstarters.dependency.Point;
import hdzi.editstarters.dependency.Repository;
import hdzi.editstarters.dependency.StarterInfo;
import hdzi.editstarters.ui.ShowErrorException;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ProjectFile<T extends PsiElement> implements EditStarters {


    @Override
    public void removeStarters(Collection<StarterInfo> dependencies) {
        try {
            T dependenciesTag = getOrCreateDependenciesTag();
            // 取已存在的依赖
            List<ProjectDependency> extDependencies = findAllDependencies(dependenciesTag);
            // 转化待删除的依赖成字符串形式，方便对比
            Set<String> removeDependencies = dependencies.stream().map(Point::point).collect(Collectors.toSet());
            // 遍历存在的依赖，如果待删除的依赖包含它，就删除
            for (ProjectDependency extDependency : extDependencies) {
                if (removeDependencies.contains(extDependency.point())) {
                    PsiElement element = extDependency.getElement();
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
            T dependenciesTag = getOrCreateDependenciesTag();
            for (StarterInfo dependency : dependencies) {
                createDependencyTag(dependenciesTag, dependency);
                Bom bom = dependency.getBom();
                if (bom != null) {
                    addBom(bom);
                }

                List<Repository> repositories = dependency.getRepositories();
                if (CollectionUtils.isNotEmpty(repositories)) {
                    addRepositories(repositories);
                }
            }
        } catch (ShowErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new ShowErrorException("Syntax error!", e);
        }
    }

    /**
     * 添加bom信息
     */
    private void addBom(Bom bom) {
        T bomTag = getOrCreateBomsTag();
        // 去重后新建
        List<ProjectBom> allBoms = findAllBoms(bomTag);
        if (allBoms.stream().noneMatch(b -> Objects.equals(b.point(), bom.point()))) {
            createBomTag(bomTag, bom);
        }
    }

    /**
     * 添加仓库信息
     */
    private void addRepositories(Collection<Repository> repositories) {
        T repositoriesTag = getOrCreateRepositoriesTag();
        List<ProjectRepository> existingRepos = findAllRepositories(repositoriesTag);
        Set<String> existingRepoPointSet = existingRepos.stream().map(Point::point).collect(Collectors.toSet());
        for (Repository repository : repositories) {
            if (!existingRepoPointSet.contains(repository.point())) {
                createRepositoryTag(repositoriesTag, repository);
            }
        }
    }

    protected abstract T getOrCreateDependenciesTag();

    protected abstract List<ProjectDependency> findAllDependencies(T dependenciesTag);

    protected abstract void createDependencyTag(T dependenciesTag, StarterInfo info);

    protected abstract T getOrCreateBomsTag();

    protected abstract List<ProjectBom> findAllBoms(T bomsTag);

    protected abstract void createBomTag(T bomsTag, Bom bom);

    protected abstract T getOrCreateRepositoriesTag();

    protected abstract List<ProjectRepository> findAllRepositories(T repositoriesTag);

    protected abstract void createRepositoryTag(T repositoriesTag, Repository repository);
}
