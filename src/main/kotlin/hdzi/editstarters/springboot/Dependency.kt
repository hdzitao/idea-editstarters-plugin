package hdzi.editstarters.springboot

/**
 * Created by taojinhou on 2019/1/16.
 */
interface Dependency : Point {
    val groupId: String
    val artifactId: String

    override val point: String
        get() = "$groupId:$artifactId"
}