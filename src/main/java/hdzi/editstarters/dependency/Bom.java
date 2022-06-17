package hdzi.editstarters.dependency;

public interface Bom extends Point {
    String getGroupId();

    String getArtifactId();

    @Override
    default String point() {
        return getGroupId() + ":" + getArtifactId();
    }
}