package hdzi.editstarters.springboot.bean

class DepResponse {

    var repositories: Map<String, Repository>? = null
    var boms: Map<String, Bom>? = null
    var dependencies: Map<String, Dependency>? = null

    class Dependency {
        var groupId: String? = null
        var artifactId: String? = null
        var scope: String? = null
        var version: String? = null
        var repository: String? = null
        var bom: String? = null
    }

    class Bom {
        var groupId: String? = null
        var artifactId: String? = null
        var version: String? = null
        var repositories: List<String>? = null
    }

    class Repository {
        var id: String? = null
        var name: String? = null
        var url: String? = null
        var snapshotEnabled: Boolean? = null

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Repository

            if (id != other.id) return false

            return true
        }

        override fun hashCode(): Int {
            return id?.hashCode() ?: 0
        }

    }
}
