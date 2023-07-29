package io.github.hdzitao.editstarters.ui;

/**
 * 自定义的错误信息
 *
 * @version 3.2.0
 */
public class ShowErrorException extends RuntimeException {
    public ShowErrorException(String message) {
        super(message);
    }

    public ShowErrorException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public static ShowErrorException internal() {
        return new ShowErrorException("!!! internal error !!!");
    }
}