package hdzi.editstarters.ui;

/**
 * 自定义的错误信息
 */
public class ShowErrorException extends RuntimeException {
    public ShowErrorException(String message) {
        super(message);
    }
}