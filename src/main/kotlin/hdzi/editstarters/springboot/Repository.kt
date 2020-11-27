package hdzi.editstarters.springboot

interface Repository : Point {
    val url: String

    override val point: String
        get() = url
}