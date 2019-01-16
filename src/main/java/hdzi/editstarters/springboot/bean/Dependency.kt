package hdzi.editstarters.springboot.bean

/**
 * Created by taojinhou on 2019/1/16.
 */
interface Dependency {
    val groupId: String?
    val artifactId: String?

    val point: String
        get() = "${groupId}:${artifactId}"
}