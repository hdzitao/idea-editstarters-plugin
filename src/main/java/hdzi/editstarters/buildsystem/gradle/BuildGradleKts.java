package hdzi.editstarters.buildsystem.gradle;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import hdzi.editstarters.buildsystem.ProjectBom;
import hdzi.editstarters.buildsystem.ProjectDependency;
import hdzi.editstarters.buildsystem.ProjectRepository;
import hdzi.editstarters.initializr.InitializrBom;
import hdzi.editstarters.initializr.InitializrRepository;
import hdzi.editstarters.initializr.StarterInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
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
                    Pair<String, String> pair = getDependencyGroupArtifact(it);
                    String groupId = pair.getLeft();
                    String artifactId = pair.getRight();
                    return new ProjectDependency(groupId, artifactId, it);
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
                    Pair<String, String> pair = splitGroupArtifact(getCallFirstParam(it));
                    return new ProjectBom(pair.getLeft(), pair.getRight());
                }).collect(Collectors.toList());
    }

    @Override
    public void createBomTag(KtBlockExpression bomsTag, InitializrBom bom) {
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
                    if (!ContainerUtil.isEmpty(arguments)) {
                        KtValueArgument first = arguments.get(0);
                        if (KtLambdaArgument.class.equals(first.getClass())) {
                            List<KtExpression> statements = ((KtLambdaArgument) first).getLambdaExpression().getBodyExpression().getStatements();
                            KtExpression urlStatement = ContainerUtil.find(statements, statement -> statement instanceof KtBinaryExpression
                                    && "url".equals(((KtBinaryExpression) statement).getLeft().getText()));
                            url = getCallFirstParam(((KtCallExpression) ((KtBinaryExpression) urlStatement).getRight()));
                        } else if (KtValueArgument.class.equals(first.getClass())) {
                            url = getCallFirstParam(it);
                        }
                    }
                    return new ProjectRepository(url);
                }).collect(Collectors.toList());
    }

    @Override
    public void createRepositoryTag(KtBlockExpression repositoriesTag, InitializrRepository repository) {
        Instruction instruction = repositoryInstruction(repository);
        addExpression(repositoriesTag, instruction.toInstString("$inst { url = uri(\"$point\") }"));
    }

    private KtBlockExpression getOrCreateTopBlock(String name) {
        Pattern regex = callNameRegex(name);
        KtScriptInitializer initializer = ContainerUtil.find(PsiTreeUtil.findChildrenOfAnyType(buildFile, KtScriptInitializer.class),
                ktScriptInitializer -> regex.matcher(ktScriptInitializer.getText()).find());

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

    private Pair<String, String> getDependencyGroupArtifact(KtCallExpression ktCallExpression) {
        Map<String, String> namedArguments = ktCallExpression.getValueArguments().stream().collect(Collectors.toMap(
                c -> trimText(c.getArgumentName().getText()),
                c -> trimText(c.getArgumentExpression().getText())
        ));
        namedArguments.remove(null);

        if (namedArguments.isEmpty()) {
            return splitGroupArtifact(getCallFirstParam(ktCallExpression));
        } else {
            return Pair.of(namedArguments.get("group"), namedArguments.get("name"));
        }
    }

    private String trimText(String s) {
        return trimText(s, '"');
    }

    private PsiElement addExpression(PsiElement psiElement, String text) {
        KtExpression ktExpression = factory.createExpression(text);
        if (ktExpression.getPrevSibling() != null
                && StringUtils.isNoneBlank(ktExpression.getPrevSibling().getText())) {
            ktExpression.getParent().addBefore(factory.createNewLine(1), ktExpression);
        }

        if (ktExpression.getNextSibling() != null
                && StringUtils.isNoneBlank(ktExpression.getNextSibling().getText())) {
            ktExpression.getParent().addAfter(factory.createNewLine(1), ktExpression);
        }

        return psiElement.add(ktExpression);
    }
}