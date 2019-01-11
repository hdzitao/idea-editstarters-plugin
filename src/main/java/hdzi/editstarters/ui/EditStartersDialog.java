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

        this.frame = new JFrame(this.title);
        this.frame.setContentPane(this.root);

        // boot版本选框
        this.versionComboBox.setModel(new CollectionComboBoxModel(
                initializr.getVersion().getValues(),
                springBoot.getCurrentVersion()));
        this.versionComboBox.setEnabled(false);

        // 取消按钮
        this.buttonCancel.addActionListener(e -> this.frame.dispose());

        // ok按钮
        this.buttonOK.addActionListener(e -> {
            WriteCommandAction.runWriteCommandAction(springBoot.getContext().getData(DataKeys.PROJECT), () -> {
                springBoot.addDependencies(this.addStarters);
                springBoot.removeDependencies(this.removeStarters);
            });
            this.frame.dispose();
        });

        LinkedHashMap<String, List<StarterInfo>> modulesMap = initializr.getModulesMap();

        // Module列表
        this.moduleList.setModel(new CollectionListModel(modulesMap.keySet()));
        this.moduleList.addListSelectionListener(e -> {
            String name = (String) ((JList) e.getSource()).getSelectedValue();
            this.starterList.setModel(new CollectionListModel(modulesMap.get(name)));
            this.starterDescPan.setText(null);
        });
        // Starter列表
        this.starterList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                StarterInfo starterInfo = (StarterInfo) ((JList) e.getSource()).getSelectedValue();

                switch (e.getClickCount()) {
                    case 1: // 按一下显示信息
                        starterDescPan.setText(starterInfo.getDescription());
                        break;
                    case 2: // 按两下选择
                        if (starterInfo.getExist()) { // 对于已存在的starter，添加就是从删除列表里删除
                            removeStarters.remove(starterInfo);
                        } else { // 对于不存在的starter，添加直接加入添加列表
                            addStarters.add(starterInfo);
                        }
                        // 去重显示
                        CollectionListModel<Object> listModel = (CollectionListModel) selectList.getModel();
                        if (!listModel.contains(starterInfo)) {
                            listModel.add(starterInfo);
                        }
                        break;
                }
            }
        });
        // selected列表
        this.selectList.setModel(new CollectionListModel(initializr.getExistStarters()));
        this.selectList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // 按两下删除
                    StarterInfo starterInfo = (StarterInfo) ((JList) e.getSource()).getSelectedValue();
                    if (starterInfo.getExist()) { // 对于已存在的starter，删除就是加入删除列表
                        removeStarters.add(starterInfo);
                    } else { // 对于不存在的starter，删除是从添加列表里删除
                        addStarters.remove(starterInfo);
                    }
                    // 显示
                    ((CollectionListModel) selectList.getModel()).remove(starterInfo);
                }
            }
        });

    }

    public void show() {
        this.frame.pack();
        this.frame.setLocationRelativeTo(null); // 中间显示
        this.frame.setVisible(true);
    }
}
