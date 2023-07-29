package io.github.hdzitao.editstarters.startspringio.metadata;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * dependencies.content
 *
 * @version 3.2.0
 */
@Getter
@Setter
@NoArgsConstructor
public class DependenciesContent {
    private String name;
    private List<MetadataDependency> content;
}
