package io.github.hdzitao.editstarters.buildsystem.maven

import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.psi.xml.XmlFile
import io.github.hdzitao.editstarters.buildsystem.BuildSystem
import io.github.hdzitao.editstarters.dependency.Dependency
import io.github.hdzitao.editstarters.ui.ShowErrorException
import org.jetbrains.idea.maven.model.MavenArtifact
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil

/**
 * maven构建系统
 *
 * @version 3.2.0
 */
class MavenBuildSystem private constructor(pomXml: PomXml, dependencies: List<Dependency>) :
    BuildSystem(pomXml, dependencies) {

    companion object {
        /**
         * 构建 maven build system
         */
        fun from(context: DataContext): MavenBuildSystem {
            val psiFile = context.getData(CommonDataKeys.PSI_FILE) as? XmlFile
                ?: throw ShowErrorException("Not an XML file!")
            val pomXml = PomXml(psiFile)
            val mavenProject = MavenActionUtil.getMavenProject(context)
                ?: throw ShowErrorException("Not a maven project!")
            val dependencies = mavenProject.dependencies
                .map { d: MavenArtifact -> Dependency(d.groupId, d.artifactId, d.baseVersion) }
                .toList()
            return MavenBuildSystem(pomXml, dependencies)
        }
    }
}