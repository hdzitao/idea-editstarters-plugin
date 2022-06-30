package hdzi.editstarters.initializr;

import hdzi.editstarters.dependency.SpringBoot;

public class OthersInitializr implements Initializr {

    @Override
    public SpringBoot initialize(InitializrParameters parameters, InitializrStatus status, InitializrChain chain) {
        OthersHub othersHub = parameters.getOthersHub();

        status.setEnableOHub(true);
        status.setOHubName(othersHub.toString());

        othersHub.initConfigure();
        StartSpringIO startSpringIO = new StartSpringIO(parameters.getVersion());
        startSpringIO.setMetadataConfig(othersHub.getMetaDataConfig());
        return new SpringBoot(parameters.getVersion(), startSpringIO.getDeclaredModules());
    }
}
