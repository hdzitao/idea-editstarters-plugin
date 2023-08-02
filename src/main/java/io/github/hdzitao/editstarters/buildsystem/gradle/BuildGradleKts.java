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
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.kotlin.psi.*;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * build.gradle.kts
 * 参考 https://github.com/JetBrains/kotlin/blob/master/idea/idea-gradle/src/org/jetbrains/kotlin/idea/configuration/KotlinBuildScriptManipulator.kt
 *
 * @version 3.2.0
 */
@SuppressWarnings("ConstantConditions")
class BuildGradleKts extends AbstractBuildGradle<KtBlockExpression> {
    private final KtFile buildFile;
    private final KtPsiFactory factory;

    public BuildGradleKts(Project project, KtFile buildFile) {
        this.buildFile = buildFile;
        this.factory = new KtPsiFactory(project);
    }

    @Override
    public KtBlockExpression findOrCreateDependenciesTag() {
        return getOrCreateTopBlock(TAG_DEPENDENCY_MANAGEMENT);
    }

    @Override
    public List<Dependency> findAllDependencies(KtBlockExpression dependenciesTag) {
        return PsiTreeUtil.getChildrenOfTypeAsList(dependenciesTag, KtCallExpression.class).stream()
                .map(this::getDependencyGroupArtifact)
                .collect(Collectors.toList());
    }

    @Override
    public void createDependencyTag(KtBlockExpression dependenciesTag, Starter starter) {
        List<Instruction> instructions = dependencyInstruction(starter);
        for (Instruction instruction : instructions) {
            addExpression(dependenciesTag, instruction.toInstString("${inst}(\"${point}\")"));
        }
    }

    @Override
    public KtBlockExpression findOrCreateBomsTag() {
        return getOrCreateBlock(getOrCreateTopBlock(TAG_BOM_MANAGEMENT), TAG_BOM_IMPORT);
    }

    @Override
    public List<Bom> findAllBoms(KtBlockExpression bomsTag) {
        return findAllCallExpression(bomsTag, TAG_BOM).stream()
                .map(tag -> newByGroupArtifact(getCallFirstParam(tag), Bom::new))
                .collect(Collectors.toList());
    }

    @Override
    public void createBomTag(KtBlockExpression bomsTag, Bom bom) {
        Instruction instruction = bomInstruction(bom);
        addExpression(bomsTag, instruction.toInstString("${inst}(\"${point}\")"));
    }

    @Override
    public KtBlockExpression findOrCreateRepositoriesTag() {
        return getOrCreateTopBlock(TAG_REPOSITORY_MANAGEMENT);
    }

    @Override
    public List<Repository> findAllRepositories(KtBlockExpression repositoriesTag) {
        return findAllCallExpression(repositoriesTag, TAG_REPOSITORY).stream()
                .map(tag -> {
                    List<KtValueArgument> arguments = tag.getValueArguments();
                    KtValueArgument first = ContainerUtil.getFirstItem(arguments);
                    if (first == null) {
                        return EMPTY;
                    }

                    if (first instanceof KtLambdaArgument) {
                        List<KtExpression> statements = ((KtLambdaArgument) first).getLambdaExpression()
                                .getBodyExpression().getStatements();
                        KtExpression urlStatement = ContainerUtil.find(statements, statement ->
                                statement instanceof KtBinaryExpression
                                        && "url".equals(((KtBinaryExpression) statement).getLeft().getText()));
                        if (urlStatement == null) {
                            return EMPTY;
                        }
                        return getCallFirstParam(((KtCallExpression) ((KtBinaryExpression) urlStatement).getRight()));
                    } else {
                        return getCallFirstParam(tag);
                    }
                })
                .map(Repository::new)
                .collect(Collectors.toList());
    }

    @Override
    public void createRepositoryTag(KtBlockExpression repositoriesTag, Repository repository) {
        Instruction instruction = repositoryInstruction(repository);
        addExpression(repositoriesTag, instruction.toInstString("${inst} { url = uri(\"${point}\") }"));
    }

    /**
     * 获取或创建顶层闭包
     *
     * @param name
     * @return
     */
    private KtBlockExpression getOrCreateTopBlock(String name) {
        Pattern regex = callNameRegex(name);
        KtScriptInitializer initializer = ContainerUtil.find(PsiTreeUtil.findChildrenOfAnyType(buildFile, KtScriptInitializer.class),
                it -> regex.matcher(it.getText()).find());

        KtCallExpression expression;
        if (initializer == null) {
            expression = (KtCallExpression) addExpression(buildFile, name + " {\n}");
        } else {
            expression = PsiTreeUtil.findChildOfType(initializer, KtCallExpression.class);
        }

        return ((KtLambdaExpression) expression.getLambdaArguments().get(0).getArgumentExpression()).getBodyExpression();
    }

    /**
     * 获取或创建闭包
     *
     * @param psiElement
     * @param name
     * @return
     */
    private KtBlockExpression getOrCreateBlock(PsiElement psiElement, String name) {
        KtCallExpression block = findCallExpression(psiElement, name);
        if (block == null) {
            block = (KtCallExpression) addExpression(psiElement, name + " {\n}");
        }

        return ((KtLambdaExpression) block.getLambdaArguments().get(0).getArgumentExpression()).getBodyExpression();
    }

    /**
     * 查找语句
     *
     * @param psiElement
     * @param name
     * @return
     */
    private KtCallExpression findCallExpression(PsiElement psiElement, String name) {
        Pattern pattern = callNameRegex(name);
        List<KtCallExpression> blocks = PsiTreeUtil.getChildrenOfTypeAsList(psiElement, KtCallExpression.class);
        return ContainerUtil.find(blocks, expression -> pattern.matcher(expression.getText()).find());
    }

    /**
     * 查找语句/批量
     *
     * @param psiElement
     * @param name
     * @return
     */
    private List<KtCallExpression> findAllCallExpression(PsiElement psiElement, String name) {
        Pattern pattern = callNameRegex(name);
        List<KtCallExpression> blocks = PsiTreeUtil.getChildrenOfTypeAsList(psiElement, KtCallExpression.class);
        return ContainerUtil.findAll(blocks, expression -> pattern.matcher(expression.getText()).find());
    }

    private Pattern callNameRegex(String s) {
        return Pattern.compile("^" + s + "\\W");
    }

    /**
     * 第一参数
     *
     * @param ktCallExpression
     * @return
     */
    private String getCallFirstParam(KtCallExpression ktCallExpression) {
        return trimQuotation(ktCallExpression.getValueArguments().get(0).getText());
    }

    /**
     * 获取依赖
     *
     * @param ktCallExpression
     * @return
     */
    private DependencyElement<KtCallExpression> getDependencyGroupArtifact(KtCallExpression ktCallExpression) {
        Map<String, String> namedArguments = ktCallExpression.getValueArguments().stream()
                .filter(argument -> argument.getArgumentName() != null)
                .collect(Collectors.toMap(
                        argument -> trimQuotation(argument.getArgumentName().getText()),
                        argument -> trimQuotation(argument.getArgumentExpression().getText())
                ));

        if (namedArguments.isEmpty()) {
            return newByGroupArtifact(getCallFirstParam(ktCallExpression), (groupId, artifactId) ->
                    new DependencyElement<>(groupId, artifactId, ktCallExpression));
        } else {
            return new DependencyElement<>(namedArguments.get("group"), namedArguments.get("name"), ktCallExpression);
        }
    }

    /**
     * 删除首尾引号
     *
     * @param s
     * @return
     */
    private String trimQuotation(String s) {
        return trimText(s, '"');
    }

    /**
     * 添加语句
     *
     * @param psiElement
     * @param text
     * @return
     */
    private PsiElement addExpression(PsiElement psiElement, String text) {
        PsiElement addEle = psiElement.add(factory.createExpression(text));

        if (addEle.getPrevSibling() != null
                && StringUtils.isNoneBlank(addEle.getPrevSibling().getText())) {
            addEle.getParent().addBefore(factory.createNewLine(1), addEle);
        }

        if (addEle.getNextSibling() != null
                && StringUtils.isNoneBlank(addEle.getNextSibling().getText())) {
            addEle.getParent().addAfter(factory.createNewLine(1), addEle);
        }

        return addEle;
    }
}