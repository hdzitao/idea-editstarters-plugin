package hdzi.editstarters.initializr;

import hdzi.editstarters.dependency.SpringBoot;

public class OthersInitializr implements Initializr {

    @Override
    public SpringBoot initialize(InitializrParameters parameters, InitializrChain chain) {
        OthersHub othersHub = parameters.getOthersHub();
        StartSpringIO startSpringIO = new StartSpringIO();

        startSpringIO.setMetaData(parameters.getVersion(), othersHub.getMetaData());
        startSpringIO.setDependencies(othersHub.getDependencies());

        return new SpringBoot(parameters.getVersion().toVersionID(), startSpringIO.getModules());
    }
}
