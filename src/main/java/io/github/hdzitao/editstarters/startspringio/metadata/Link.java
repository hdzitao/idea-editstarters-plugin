package io.github.hdzitao.editstarters.startspringio.metadata;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 链接
 *
 * @version 3.2.0
 */
@Getter
@Setter
@NoArgsConstructor
public class Link {
    private String rel;
    private String href;
    private boolean templated;
    private String title;
    private String description;
}
