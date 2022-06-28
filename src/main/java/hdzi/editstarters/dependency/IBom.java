package hdzi.editstarters.dependency;

public interface IBom extends Point {
    String getGroupId();

    String getArtifactId();

    String getVersion();

    @Override
    default String point() {
        return getGroupId() + ":" + getArtifactId();
    }
}