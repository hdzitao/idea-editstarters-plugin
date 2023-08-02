package io.github.hdzitao.editstarters.buildsystem.gradle;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import io.github.hdzitao.editstarters.buildsystem.DependencyElement;
import io.github.hdzitao.editstarters.dependency.Bom;
import io.github.hdzitao.editstarters.dependency.Dependency;
import io.github.hdzitao.editstarters.dependency.Repository;
import io.github.hdzitao.editstarters.springboot.Starter;
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile;
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.GrStatement;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.blocks.GrClosableBlock;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * build.gradle
 *
 * @version 3.2.0
 */
@SuppressWarnings("ConstantConditions")
public class BuildGradle extends AbstractBuildGradle<GrClosableBlock> {
    private final GroovyFile buildFile;
    private final GroovyPsiElementFactory factory;

    public BuildGradle(Project project, GroovyFile buildFile) {
        this.buildFile = buildFile;
        this.factory = GroovyPsiElementFactory.getInstance(project);
    }

    @Override
    public GrClosableBlock findOrCreateDependenciesTag() {
        return getOrCreateClosure(buildFile, TAG_DEPENDENCY_MANAGEMENT);
    }

    @Override
    public List<Dependency> findAllDependencies(GrClosableBlock dependenciesTag) {
        return PsiTreeUtil.getChildrenOfTypeAsList(dependenciesTag, GrMethodCall.class).stream()
                .map(this::getDependencyGroupArtifact)
                .collect(Collectors.toList());
    }


    @Override
    public void createDependencyTag(GrClosableBlock dependenciesTag, Starter starter) {
        List<Instruction> instructions = dependencyInstruction(starter);
        for (Instruction instruction : instructions) {
            GrStatement statement = factory.createStatementFromText(instruction.toInstString("${inst} '${point}'"));
            dependenciesTag.addStatementBefore(statement, null);
        }
    }

    @Override
    public GrClosableBlock findOrCreateBomsTag() {
        return getOrCreateClosure(getOrCreateClosure(buildFile, TAG_BOM_MANAGEMENT), TAG_BOM_IMPORT);
    }

    @Override
    public List<Bom> findAllBoms(GrClosableBlock bomsTag) {
        return findAllMethod(bomsTag, TAG_BOM).stream()
                .map(tag -> newByGroupArtifact(getMethodFirstParam(tag), Bom::new))
                .collect(Collectors.toList());
    }

    @Override
    public void createBomTag(GrClosableBlock bomsTag, Bom bom) {
        Instruction instruction = bomInstruction(bom);
        GrStatement statement = factory.createStatementFromText(instruction.toInstString("${inst} '${point}'"));
        bomsTag.addStatementBefore(statement, null);
    }

    @Override
    public GrClosableBlock findOrCreateRepositoriesTag() {
        return getOrCreateClosure(buildFile, TAG_REPOSITORY_MANAGEMENT);
    }


    @Override
    public List<Repository> findAllRepositories(GrClosableBlock repositoriesTag) {
        return findAllMethod(repositoriesTag, TAG_REPOSITORY).stream()
                .map(tag -> {
                    GrMethodCall urlCall = findMethod(tag.getClosureArguments()[0], "url");
                    return new Repository(urlCall != null ? getMethodFirstParam(urlCall) : "");
                }).collect(Collectors.toList());
    }

    @Override
    public void createRepositoryTag(GrClosableBlock repositoriesTag, Repository repository) {
        Instruction instruction = repositoryInstruction(repository);
        GrStatement statement = factory.createStatementFromText(instruction.toInstString("${inst} { url '${point}' }"));
        repositoriesTag.addStatementBefore(statement, null);
    }

    /**
     * 闭包的获取或创建
     *
     * @param psiElement
     * @param name
     * @return
     */
    private GrClosableBlock getOrCreateClosure(PsiElement psiElement, String name) {
        GrMethodCall closure = findMethod(psiElement, name);
        if (closure == null) {
            GrStatement statement = factory.createStatementFromText(name + " {\n}");
            if (psiElement instanceof GrClosableBlock) {
                closure = (GrMethodCall) ((GrClosableBlock) psiElement).addStatementBefore(statement, null);
            } else {
                closure = (GrMethodCall) psiElement.add(statement);
            }
        }

        return closure.getClosureArguments()[0];
    }

    /**
     * 查找方法
     *
     * @param psiElement
     * @param name
     * @return
     */
    private GrMethodCall findMethod(PsiElement psiElement, String name) {
        return ContainerUtil.find(PsiTreeUtil.getChildrenOfTypeAsList(psiElement, GrMethodCall.class), call ->
                Objects.equals(name, call.getInvokedExpression().getText()));
    }

    /**
     * 查找方法/批量
     *
     * @param psiElement
     * @param name
     * @return
     */
    private List<GrMethodCall> findAllMethod(PsiElement psiElement, String name) {
        List<GrMethodCall> closableBlocks = PsiTreeUtil.getChildrenOfTypeAsList(psiElement, GrMethodCall.class);
        return ContainerUtil.findAll(closableBlocks, call -> Objects.equals(name, call.getInvokedExpression().getText()));
    }

    /**
     * 获取方法第一个参数
     *
     * @param call
     * @return
     */
    private String getMethodFirstParam(GrMethodCall call) {
        return trimQuotation(call.getArgumentList().getAllArguments()[0].getText());
    }

    /**
     * 解析依赖语句
     *
     * @param call
     * @return
     */
    private DependencyElement<GrMethodCall> getDependencyGroupArtifact(GrMethodCall call) {
        Map<String, String> namedArguments = Arrays.stream(call.getNamedArguments()).collect(Collectors.toMap(
                argument -> trimQuotation(argument.getLabel().getText()),
                argument -> trimQuotation(argument.getExpression().getText())
        ));

        if (namedArguments.isEmpty()) {
            return newByGroupArtifact(getMethodFirstParam(call), (groupId, artifactId) ->
                    new DependencyElement<>(groupId, artifactId, call));
        } else {
            return new DependencyElement<>(namedArguments.get("group"), namedArguments.get("name"), call);
        }
    }

    /**
     * 删除头尾的引号
     *
     * @param s
     * @return
     */
    private String trimQuotation(String s) {
        return trimText(s, '\'', '"');
    }
}
