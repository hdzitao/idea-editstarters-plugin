package io.github.hdzitao.editstarters.buildsystem;

import com.intellij.psi.PsiElement;
import io.github.hdzitao.editstarters.dependency.Dependency;

/**
 * 带PsiElement元素的dependency
 *
 * @version 3.2.0
 */
public class DependencyElement<Psi extends PsiElement> extends Dependency {
    private Psi element;

    public DependencyElement(String groupId, String artifactId, Psi element) {
        super(groupId, artifactId);
        this.element = element;
    }

    public Psi getElement() {
        return element;
    }

    public void setElement(Psi element) {
        this.element = element;
    }
}