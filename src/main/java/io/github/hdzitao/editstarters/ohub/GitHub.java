package io.github.hdzitao.editstarters.ohub;

/**
 * github
 *
 * @version 3.2.0
 */
public class GitHub extends OHub {
    public static final String NAME = "GitHub";

    @Override
    protected String basePath() {
        return "https://raw.githubusercontent.com/hdzitao/idea-editstarters-plugin/master/bootVersion/";
    }

    @Override
    public String getName() {
        return NAME;
    }
}
