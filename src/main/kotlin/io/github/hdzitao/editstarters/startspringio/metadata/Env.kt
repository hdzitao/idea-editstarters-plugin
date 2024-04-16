package io.github.hdzitao.editstarters.startspringio.metadata

/**
 * configuration.env
 *
 * @version 3.2.0
 */
class Env(
    var platform: Platform? = null,
    var boms: Map<String, MetaDataBom>? = null,
    var repositories: Map<String, MetadataRepository>? = null
)
