package hdzi.editstarters.dependency;

/**
 * 依赖
 */
public interface Dependency extends Point {
    String getGroupId();

    String getArtifactId();

    @Override
    default String point() {
        return getGroupId() + ":" + getArtifactId();
    }
}