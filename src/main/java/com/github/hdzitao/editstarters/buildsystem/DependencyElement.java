package com.github.hdzitao.editstarters.buildsystem;

import com.github.hdzitao.editstarters.dependency.Dependency;
import com.intellij.psi.PsiElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DependencyElement extends Dependency {
    private PsiElement element;

    public DependencyElement(String groupId, String artifactId, String version, PsiElement element) {
        super(groupId, artifactId, version);
        this.element = element;
    }

    public DependencyElement(String groupId, String artifactId, PsiElement element) {
        this(groupId, artifactId, null, element);
    }
}