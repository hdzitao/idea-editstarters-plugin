package hdzi.editstarters.initializr.chain;

import hdzi.editstarters.dependency.SpringBoot;
import hdzi.editstarters.ui.ShowErrorException;

public class InitializrChain {
    private static final Initializr[] CHAIN = {
            // todo 先取消缓存方便测试 new CacheInitializr(), // 通过缓存处理
            new SpringInitializr() // 最后的默认处理
    };
    private int i = 0;

    public SpringBoot initialize(InitializrParameters parameters) {
        if (i >= CHAIN.length) {
            throw new ShowErrorException("internal error!!!");
        }
        return CHAIN[i++].initialize(parameters, this);
    }
}
