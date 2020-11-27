package hdzi.editstarters.springboot.initializr

class InitializrVersion {
    class Value {
        lateinit var id: String
        lateinit var name: String
        override fun toString(): String {
            return name
        }
    }

    lateinit var default: String
    var values: List<Value>? = null
}

