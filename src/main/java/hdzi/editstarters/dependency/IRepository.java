package hdzi.editstarters.dependency;

/**
 * 仓库
 */
public interface IRepository extends Point {
    String getId();

    String getName();

    boolean isSnapshotEnabled();

    String getUrl();

    @Override
    default String point() {
        return getUrl();
    }
}