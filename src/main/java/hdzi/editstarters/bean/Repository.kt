package hdzi.editstarters.bean

interface Repository : Point {
    val url: String?

    override val point: String
        get() = url!!
}