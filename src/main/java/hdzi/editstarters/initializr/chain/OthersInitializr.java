package hdzi.editstarters.initializr.chain;

import hdzi.editstarters.dependency.SpringBoot;
import hdzi.editstarters.initializr.OthersHub;
import hdzi.editstarters.initializr.StartSpringIO;

public class OthersInitializr implements Initializr {

    @Override
    public SpringBoot initialize(InitializrParameters parameters, InitializrChain chain) {
        OthersHub othersHub = parameters.getOthersHub();
        StartSpringIO startSpringIO = new StartSpringIO();
        String versionID = parameters.getVersion().toVersionID();

        startSpringIO.setMetaData(versionID, othersHub.getMetaData());
        startSpringIO.setDependencies(othersHub.getDependencies());

        return new SpringBoot(versionID, startSpringIO.getModules());
    }
}
