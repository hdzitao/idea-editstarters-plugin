package hdzi.editstarters.springboot.bean

class Version {
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

