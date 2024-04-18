package io.github.hdzitao.editstarters.startspringio

import io.github.hdzitao.editstarters.springboot.Module
import io.github.hdzitao.editstarters.springboot.SpringBoot
import io.github.hdzitao.editstarters.springboot.SpringBootBuilder
import io.github.hdzitao.editstarters.springboot.Starter
import io.github.hdzitao.editstarters.startspringio.metadata.MetadataConfig
import io.github.hdzitao.editstarters.version.Version
import io.github.hdzitao.editstarters.version.Versions

/**
 * 从StartSpringIO构建SpringBoot
 *
 * @version 3.2.0
 */
class StartSpringIO2SpringBoot : SpringBootBuilder<StartSpringIO> {
    override fun buildSpringBoot(from: StartSpringIO): SpringBoot {
        val version = from.version
        val modules = getDeclaredModules(from.metadataConfig, version)

        return SpringBoot(version, modules)
    }

    private fun getDeclaredModules(metadataConfig: MetadataConfig, version: Version): MutableList<Module> {
        val modules: MutableList<Module> = mutableListOf()

        val configuration = metadataConfig.configuration ?: return modules
        val env = configuration.env ?: return modules

        val dependencies = metadataConfig.dependencies ?: return modules
        val dependenciesContents = dependencies.content ?: return modules

        for (dependenciesContent in dependenciesContents) {
            val name = dependenciesContent.name ?: continue

            val module = Module(name, mutableListOf())

            val dependencyContent = dependenciesContent.content ?: continue
            for (ct in dependencyContent) {
                val content = ct.resolve(version)

                if (content.groupId?.isEmpty() == true) {
                    continue
                }

                if (content.compatibilityRange?.isNotBlank() == true
                    && !Versions.parseRange(content.compatibilityRange!!).match(version)
                ) {
                    continue
                }

                val starter = Starter()

                starter.groupId = content.groupId
                starter.artifactId = content.artifactId

                starter.id = content.id
                starter.name = content.name
                starter.description = content.description
                starter.versionRange = content.compatibilityRange
                starter.version = content.version
                starter.scope = content.scope

                if (content.bom?.isNotBlank() == true) {
                    env.boms?.get(content.bom!!)?.resolve(version)?.let { bom ->
                        starter.bom = bom

                        bom.repositories?.forEach { rid ->
                            env.repositories?.get(rid)?.resolve().let { repository ->
                                starter.addRepository(rid, repository)
                            }
                        }
                    }
                }

                if (content.repository?.isNotBlank() == true) {
                    val rid = content.repository!!
                    env.repositories?.get(rid)?.resolve().let { repository ->
                        starter.addRepository(rid, repository)
                    }
                }

                module.values.add(starter)
            }

            if (module.values.isNotEmpty()) {
                modules.add(module)
            }
        }

        return modules
    }
}
