package io.github.hdzitao.editstarters.startspringio.metadata

import com.google.gson.annotations.SerializedName
import io.github.hdzitao.editstarters.dependency.Repository

/**
 * configuration.env.repositories
 *
 * @version 3.2.0
 */
class MetadataRepository(
    @field:SerializedName(value = "snapshotEnabled", alternate = ["snapshotsEnabled"])
    var snapshotEnabled: Boolean = false,
    var releasesEnabled: Boolean = false
) : Repository() {

    /**
     * 根据版本处理
     */
    fun resolve(): MetadataRepository {
        val repository = MetadataRepository()
        repository.name = this.name
        repository.url = this.url
        repository.isSnapshot = this.snapshotEnabled

        repository.snapshotEnabled = this.snapshotEnabled
        repository.releasesEnabled = this.releasesEnabled

        return repository
    }
}