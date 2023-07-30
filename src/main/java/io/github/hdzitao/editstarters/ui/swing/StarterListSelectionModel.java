package io.github.hdzitao.editstarters.ui.swing;

import io.github.hdzitao.editstarters.dependency.Starter;
import lombok.AllArgsConstructor;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * starter list的ListSelectionModel
 *
 * @version 3.2.0
 */
@AllArgsConstructor
public class StarterListSelectionModel extends EditStartersSelectionModel {
    private final JList<Starter> starterList;

    private final Consumer<Starter> selectedCallback;
    private final Consumer<Starter> cancelCallback;

    @Override
    protected void selectedCallback(int index0, int index1) {
        Starter starter = starterList.getModel().getElementAt(index0);
        selectedCallback.accept(starter);
    }

    @Override
    protected void cancelCallback(int index0, int index1) {
        Starter starter = starterList.getModel().getElementAt(index0);
        cancelCallback.accept(starter);
    }
}
