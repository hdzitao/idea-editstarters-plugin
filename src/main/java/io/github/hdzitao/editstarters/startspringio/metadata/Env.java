package io.github.hdzitao.editstarters.startspringio.metadata;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * configuration.env
 *
 * @version 3.2.0
 */
@Getter
@Setter
@NoArgsConstructor
public class Env {
    private Platform platform;

    private Map<String, MetaDataBom> boms;
    private Map<String, MetadataRepository> repositories;
}
