package hdzi.editstarters.springboot.bean


/**
 * Created by taojinhou on 2018/12/21.
 */
class StarterInfo {
    // 基本信息
    var id: String? = null
    var name: String? = null
    var description: String? = null
    var versionRange: String? = null
    // 坐标信息
    var groupId: String? = null
    var artifactId: String? = null
    var scope: String? = null
    var version: String? = null
    var repositories = mutableSetOf<DepResponse.Repository>()
    var bom: DepResponse.Bom? = null
    var exist = false

    val descDetails: String
        get() {
            val buffer = StringBuffer()
            if (groupId != null) {
                buffer.append("groupId: ${groupId}\nartifactId: ${artifactId}\nscope: ${scope}\n")
                if (version != null) {
                    buffer.append("version: ${version}\n")
                }
            } else if (versionRange != null) {
                buffer.append("versionRange: ${versionRange}\n")
            }

            buffer.append("desc: ${description}")

            return buffer.toString()
        }

    val searchKey: String
        get() = "${groupId}\t${artifactId}\t${name}\t${description}".toLowerCase()

    val canBeAdded: Boolean
        get() = groupId != null && artifactId != null

    fun addRepository(repository: DepResponse.Repository?) {
        if (repository != null) {
            this.repositories.add(repository)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StarterInfo

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return name!!
    }
}
