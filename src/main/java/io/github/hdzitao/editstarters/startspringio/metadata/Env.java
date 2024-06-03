package io.github.hdzitao.editstarters.startspringio.metadata;

import java.util.Map;

/**
 * configuration.env
 *
 * @version 3.2.0
 */
public class Env {
    private Platform platform;

    private Map<String, MetaDataBom> boms;
    private Map<String, MetadataRepository> repositories;

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public Map<String, MetaDataBom> getBoms() {
        return boms;
    }

    public void setBoms(Map<String, MetaDataBom> boms) {
        this.boms = boms;
    }

    public Map<String, MetadataRepository> getRepositories() {
        return repositories;
    }

    public void setRepositories(Map<String, MetadataRepository> repositories) {
        this.repositories = repositories;
    }
}
