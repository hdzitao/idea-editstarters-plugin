package hdzi.editstarters.springboot.initializr

class InitializrVersion {
    class Value {
        lateinit var id: String
        lateinit var name: String
    }

    lateinit var default: String
    lateinit var values: List<Value>
}

