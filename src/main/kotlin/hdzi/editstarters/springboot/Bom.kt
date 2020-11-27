package hdzi.editstarters.springboot

interface Bom : Point {
    val groupId: String?
    val artifactId: String?

    override val point: String
        get() = "$groupId:$artifactId"
}