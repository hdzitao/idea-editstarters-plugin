package hdzi.editstarters.dependency;

/**
 * 仓库
 */
public interface IRepository extends Point {
    String getId();

    void setId(String id);

    String getName();

    boolean isSnapshotEnabled();

    String getUrl();

    @Override
    default String point() {
        return getUrl();
    }
}