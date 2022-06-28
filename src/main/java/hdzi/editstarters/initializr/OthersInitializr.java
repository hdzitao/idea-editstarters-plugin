package hdzi.editstarters.initializr;

import hdzi.editstarters.dependency.SpringBoot;

public class OthersInitializr implements Initializr {

    @Override
    public SpringBoot initialize(InitializrParameters parameters, InitializrChain chain) {
        OthersHub othersHub = parameters.getOthersHub();
        othersHub.init();
        StartSpringIO startSpringIO = new StartSpringIO(parameters.getVersion());
        StartSpringIO.Mode mode = othersHub.getConfigure().getMode();
        if (StartSpringIO.Mode.CONFIG == mode) {
            startSpringIO.setMetadataConfig(othersHub.getMetaDataConfig());
        } else {
            startSpringIO.setMetadataClient(othersHub.getMetaDataClient());
            startSpringIO.setDependencies(othersHub.getDependencies());
        }

        return new SpringBoot(parameters.getVersion(), startSpringIO.getDeclaredModules());
    }
}
