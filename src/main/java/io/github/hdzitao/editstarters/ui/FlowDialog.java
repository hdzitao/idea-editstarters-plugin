package io.github.hdzitao.editstarters.ui;

/**
 * 流程对话框
 *
 * @version 4.0.0
 */
public interface FlowDialog {
    void show();

    default void next() {
    }
}
