package hdzi.editstarters.bean.initializr

class InitializrVersion(
    var default: String?,
    var values: List<Value>?
) {
    class Value {
        var id: String? = null
        var name: String? = null
        override fun toString(): String {
            return name!!
        }
    }
}

