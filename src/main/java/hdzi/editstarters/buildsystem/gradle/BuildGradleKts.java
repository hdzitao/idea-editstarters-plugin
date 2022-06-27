package hdzi.editstarters.buildsystem.gradle;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import hdzi.editstarters.buildsystem.ProjectBom;
import hdzi.editstarters.buildsystem.ProjectDependency;
import hdzi.editstarters.buildsystem.ProjectRepository;
import hdzi.editstarters.dependency.Bom;
import hdzi.editstarters.dependency.Repository;
import hdzi.editstarters.dependency.StarterInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.kotlin.psi.*;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 参考 https://github.com/JetBrains/kotlin/blob/master/idea/idea-gradle/src/org/jetbrains/kotlin/idea/configuration/KotlinBuildScriptManipulator.kt
 * <p>
 * Created by taojinhou on 2019/1/17.
 */
@SuppressWarnings("ConstantConditions")
class BuildGradleKts extends GradleSyntax<KtBlockExpression> {
    private final KtFile buildFile;

    private final KtPsiFactory factory;

    public BuildGradleKts(Project project, KtFile buildFile) {
        this.buildFile = buildFile;
        this.factory = new KtPsiFactory(project);
    }

    @Override
    public KtBlockExpression getOrCreateDependenciesTag() {
        return getOrCreateTopBlock(TAG_DEPENDENCY_MANAGEMENT);
    }

    @Override
    public List<ProjectDependency> findAllDependencies(KtBlockExpression dependenciesTag) {
        return PsiTreeUtil.getChildrenOfTypeAsList(dependenciesTag, KtCallExpression.class).stream()
                .map(it -> {
                    GradlePoint gradlePoint = getDependencyGroupArtifact(it);
                    return new ProjectDependency(gradlePoint.getGroupId(), gradlePoint.getArtifactId(), it);
                }).collect(Collectors.toList());
    }

    @Override
    public void createDependencyTag(KtBlockExpression dependenciesTag, StarterInfo info) {
        List<Instruction> instructions = dependencyInstruction(info);
        for (Instruction instruction : instructions) {
            addExpression(dependenciesTag, instruction.toInstString("$inst(\"$point\")"));
        }
    }

    @Override
    public KtBlockExpression getOrCreateBomsTag() {
        return getOrCreateBlock(getOrCreateTopBlock(TAG_BOM_MANAGEMENT), TAG_BOM_IMPORT);
    }

    @Override
    public List<ProjectBom> findAllBoms(KtBlockExpression bomsTag) {
        return findAllCallExpression(bomsTag, TAG_BOM).stream()
                .map(it -> {
                    GradlePoint gradlePoint = splitGroupArtifact(getCallFirstParam(it));
                    return new ProjectBom(gradlePoint.getGroupId(), gradlePoint.getArtifactId());
                }).collect(Collectors.toList());
    }

    @Override
    public void createBomTag(KtBlockExpression bomsTag, Bom bom) {
        Instruction instruction = bomInstruction(bom);
        addExpression(bomsTag, instruction.toInstString("$inst(\"$point\")"));
    }

    @Override
    public KtBlockExpression getOrCreateRepositoriesTag() {
        return getOrCreateTopBlock(TAG_REPOSITORY_MANAGEMENT);
    }

    @Override
    public List<ProjectRepository> findAllRepositories(KtBlockExpression repositoriesTag) {
        return findAllCallExpression(repositoriesTag, TAG_REPOSITORY).stream()
                .map(it -> {
                    List<KtValueArgument> arguments = it.getValueArguments();
                    String url = "";
                    KtValueArgument first;
                    if ((first = ContainerUtil.getFirstItem(arguments)) != null) {
                        if (first instanceof KtLambdaArgument) {
                            List<KtExpression> statements = ((KtLambdaArgument) first).getLambdaExpression().getBodyExpression().getStatements();
                            KtExpression urlStatement = ContainerUtil.find(statements, statement -> statement instanceof KtBinaryExpression
                                    && "url".equals(((KtBinaryExpression) statement).getLeft().getText()));
                            url = getCallFirstParam(((KtCallExpression) ((KtBinaryExpression) urlStatement).getRight()));
                        } else {
                            url = getCallFirstParam(it);
                        }
                    }
                    return new ProjectRepository(url);
                }).collect(Collectors.toList());
    }

    @Override
    public void createRepositoryTag(KtBlockExpression repositoriesTag, Repository repository) {
        Instruction instruction = repositoryInstruction(repository);
        addExpression(repositoriesTag, instruction.toInstString("$inst { url = uri(\"$point\") }"));
    }

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

    private KtBlockExpression getOrCreateBlock(PsiElement psiElement, String name) {
        KtCallExpression block = findCallExpression(psiElement, name);
        if (block == null) {
            block = (KtCallExpression) addExpression(psiElement, name + " {\n}");
        }

        return ((KtLambdaExpression) block.getLambdaArguments().get(0).getArgumentExpression()).getBodyExpression();
    }

    private KtCallExpression findCallExpression(PsiElement psiElement, String name) {
        Pattern pattern = callNameRegex(name);
        List<KtCallExpression> blocks = PsiTreeUtil.getChildrenOfTypeAsList(psiElement, KtCallExpression.class);
        return ContainerUtil.find(blocks, expression -> pattern.matcher(expression.getText()).find());
    }

    private List<KtCallExpression> findAllCallExpression(PsiElement psiElement, String name) {
        Pattern pattern = callNameRegex(name);
        List<KtCallExpression> blocks = PsiTreeUtil.getChildrenOfTypeAsList(psiElement, KtCallExpression.class);
        return ContainerUtil.findAll(blocks, expression -> pattern.matcher(expression.getText()).find());
    }

    private Pattern callNameRegex(String s) {
        return Pattern.compile("^" + s + "\\W");
    }

    private String getCallFirstParam(KtCallExpression ktCallExpression) {
        return trimText(ktCallExpression.getValueArguments().get(0).getText());
    }

    private GradlePoint getDependencyGroupArtifact(KtCallExpression ktCallExpression) {
        Map<String, String> namedArguments = ktCallExpression.getValueArguments().stream()
                .filter(it -> it.getArgumentName() != null)
                .collect(Collectors.toMap(
                        it -> trimText(it.getArgumentName().getText()),
                        it -> trimText(it.getArgumentExpression().getText())
                ));

        if (namedArguments.isEmpty()) {
            return splitGroupArtifact(getCallFirstParam(ktCallExpression));
        } else {
            return GradlePoint.of(namedArguments.get("group"), namedArguments.get("name"));
        }
    }

    private String trimText(String s) {
        return trimText(s, '"');
    }

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