package hdzi.editstarters.ui;

import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.CollectionListModel;
import hdzi.editstarters.springboot.SpringBootEditor;
import hdzi.editstarters.springboot.SpringInitializr;
import hdzi.editstarters.springboot.bean.StarterInfo;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unchecked")
public class EditStartersDialog {
    private JPanel root;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox versionComboBox;
    private JList moduleList;
    private JList starterList;
    private JList selectList;
    private JTextPane starterDescPan;
    private JFrame frame;
    private String title = "Edit Starters";
    private Set<StarterInfo> addStarters = new HashSet<>(64);
    private Set<StarterInfo> removeStarters = new HashSet<>(64);

    public EditStartersDialog(SpringBootEditor springBoot) {
        SpringInitializr initializr = springBoot.getSpringInitializr();

        this.versionComboBox.setModel(new CollectionComboBoxModel(
                initializr.getVersion().getValues(),
                springBoot.getCurrentVersion()));
        this.versionComboBox.setEnabled(false);

        this.frame = new JFrame(this.title);
        this.frame.setContentPane(this.root);

        this.buttonCancel.addActionListener(e -> this.frame.dispose());

        this.buttonOK.addActionListener(e -> {
            WriteCommandAction.runWriteCommandAction(springBoot.getContext().getData(DataKeys.PROJECT), () -> {
                springBoot.addDependencies(this.addStarters);
                springBoot.removeDependencies(this.removeStarters);
            });
            this.frame.dispose();
        });

        LinkedHashMap<String, List<StarterInfo>> modulesMap = initializr.getModulesMap();

        this.moduleList.setModel(new CollectionListModel(modulesMap.keySet()));
        this.selectList.setModel(new CollectionListModel(initializr.getExistStarters()));

        this.moduleList.addListSelectionListener(e -> {
            String name = (String) ((JList) e.getSource()).getSelectedValue();
            this.starterList.setModel(new CollectionListModel(modulesMap.get(name)));
            this.starterDescPan.setText(null);
        });

        this.starterList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                StarterInfo starterInfo = (StarterInfo) ((JList) e.getSource()).getSelectedValue();

                switch (e.getClickCount()) {
                    case 1:
                        starterDescPan.setText(starterInfo.getDescription());
                        break;
                    case 2:
                        if (starterInfo.getExist()) {
                            removeStarters.remove(starterInfo);
                        } else {
                            addStarters.add(starterInfo);
                        }
                        CollectionListModel<Object> listModel = (CollectionListModel) selectList.getModel();
                        if (!listModel.contains(starterInfo)) {
                            listModel.add(starterInfo);
                        }
                        selectList.setModel(listModel);
                        break;
                }
            }
        });

        this.selectList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    StarterInfo starterInfo = (StarterInfo) ((JList) e.getSource()).getSelectedValue();
                    if (starterInfo.getExist()) {
                        removeStarters.add(starterInfo);
                    } else {
                        addStarters.remove(starterInfo);
                    }

                    ((CollectionListModel) selectList.getModel()).remove(starterInfo);
                }
            }
        });

    }

    public void show() {
        this.frame.pack();
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
    }
}
