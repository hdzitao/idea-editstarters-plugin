package io.github.hdzitao.editstarters.startspringio.metadata;

import com.google.gson.annotations.SerializedName;
import io.github.hdzitao.editstarters.dependency.Repository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * configuration.env.repositories
 *
 * @version 3.2.0
 */
@Getter
@Setter
@NoArgsConstructor
public class MetadataRepository extends Repository {
    @SerializedName(value = "snapshotEnabled", alternate = "snapshotsEnabled")
    private boolean snapshotEnabled = false;
    private boolean releasesEnabled;

    /**
     * 根据版本处理
     */
    public MetadataRepository resolve() {
        MetadataRepository repository = new MetadataRepository();
        repository.name = this.name;
        repository.url = this.url;
        repository.isSnapshot = this.snapshotEnabled;

        repository.snapshotEnabled = this.snapshotEnabled;
        repository.releasesEnabled = this.releasesEnabled;

        return repository;
    }
}
