package io.github.hdzitao.editstarters.ui.swing;

import io.github.hdzitao.editstarters.springboot.Starter;

/**
 * starter/selected列表关于starter的处理
 * 借此解构列表本身和starter处理的关联
 *
 * @version 3.2.1
 */
public interface StarterProcessor<R> {
    R process(Starter starter);
}
