package io.github.hdzitao.editstarters.ohub;

import io.github.hdzitao.editstarters.version.Version;

/**
 * github
 *
 * @version 3.2.0
 */
public class GitHub extends OHub {
    public GitHub(String site, Version version) {
        super(site, version);
    }

    @Override
    protected String basePath() {
        return "https://raw.githubusercontent.com/hdzitao/idea-editstarters-plugin/master/bootVersion/";
    }

    @Override
    public String getName() {
        return "GitHub";
    }
}
