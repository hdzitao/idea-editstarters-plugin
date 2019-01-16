package hdzi.editstarters.bean.initializr

class InitializrVersion {
    var default: String? = null
    var values: List<Value>? = null

    class Value {
        var id: String? = null
        var name: String? = null
        override fun toString(): String {
            return name!!
        }
    }
}

