package io.github.hdzitao.editstarters.ohub;

/**
 * gitee
 *
 * @version 3.2.0
 */
public class Gitee extends OHub {
    @Override
    protected String basePath() {
        return "https://gitee.com/hdzitao/idea-editstarters-plugin/raw/master/bootVersion/";
    }

    @Override
    public String getName() {
        return "Gitee";
    }
}
