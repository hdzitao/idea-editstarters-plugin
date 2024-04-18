package io.github.hdzitao.editstarters.buildsystem

import com.intellij.psi.PsiElement
import io.github.hdzitao.editstarters.dependency.Dependency

/**
 * 带PsiElement元素的dependency
 *
 * @version 3.2.0
 */
class DependencyElement<Psi : PsiElement>(groupId: String, artifactId: String, val element: Psi) :
    Dependency(groupId, artifactId)