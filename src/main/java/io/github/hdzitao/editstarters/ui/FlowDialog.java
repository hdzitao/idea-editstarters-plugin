package io.github.hdzitao.editstarters.ui;

/**
 * 流程对话框
 *
 * @version 4.0.0
 */
public interface FlowDialog {

    /**
     * 展示
     */
    void show();

    /**
     * 生成下一个对话框
     *
     * @return
     */
    default void next() {
        throw ShowErrorException.internal();
    }
}
