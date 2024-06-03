package io.github.hdzitao.editstarters.startspringio.metadata;

import com.google.gson.annotations.SerializedName;
import io.github.hdzitao.editstarters.dependency.Repository;

/**
 * configuration.env.repositories
 *
 * @version 3.2.0
 */
public class MetadataRepository extends Repository {
    @SerializedName(value = "snapshotEnabled", alternate = "snapshotsEnabled")
    private boolean snapshotEnabled = false;
    private boolean releasesEnabled;

    public boolean isSnapshotEnabled() {
        return snapshotEnabled;
    }

    public void setSnapshotEnabled(boolean snapshotEnabled) {
        this.snapshotEnabled = snapshotEnabled;
    }

    public boolean isReleasesEnabled() {
        return releasesEnabled;
    }

    public void setReleasesEnabled(boolean releasesEnabled) {
        this.releasesEnabled = releasesEnabled;
    }

    /**
     * 根据版本处理
     */
    public MetadataRepository resolve() {
        MetadataRepository repository = new MetadataRepository();
        repository.name = this.name;
        repository.url = this.url;
        repository.snapshot = this.snapshotEnabled;

        repository.snapshotEnabled = this.snapshotEnabled;
        repository.releasesEnabled = this.releasesEnabled;

        return repository;
    }
}
