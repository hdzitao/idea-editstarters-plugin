package hdzi.editstarters.dependency;

/**
 * 依赖
 */
public interface IDependency extends Point {
    String getGroupId();

    String getArtifactId();

    @Override
    default String point() {
        return getGroupId() + ":" + getArtifactId();
    }
}