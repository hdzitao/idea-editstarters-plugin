package hdzi.editstarters.bean

interface Bom : Point {
    val groupId: String?
    val artifactId: String?

    override val point: String
        get() = "$groupId:$artifactId"
}