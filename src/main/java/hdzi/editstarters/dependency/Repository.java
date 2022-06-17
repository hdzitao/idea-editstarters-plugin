package hdzi.editstarters.dependency;

/**
 * 仓库
 */
public interface Repository extends Point {
    String getUrl();

    @Override
    default String point() {
        return getUrl();
    }
}