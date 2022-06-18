package hdzi.editstarters.buildsystem;

import com.intellij.psi.PsiElement;
import hdzi.editstarters.dependency.Dependency;
import lombok.Data;

/**
 * Created by taojinhou on 2019/1/15.
 */
@Data
public class ProjectDependency implements Dependency {
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final PsiElement element;

    public ProjectDependency(String groupId, String artifactId, String version, PsiElement element) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.element = element;
    }

    public ProjectDependency(String groupId, String artifactId) {
        this(groupId, artifactId, null, null);
    }

    public ProjectDependency(String groupId, String artifactId, String version) {
        this(groupId, artifactId, version, null);
    }

    public ProjectDependency(String groupId, String artifactId, PsiElement element) {
        this(groupId, artifactId, null, element);
    }
}