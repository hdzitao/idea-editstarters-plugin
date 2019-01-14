package hdzi.editstarters.ui;

import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.CollectionListModel;
import hdzi.editstarters.springboot.SpringBootEditor;
import hdzi.editstarters.springboot.SpringInitializr;
import hdzi.editstarters.springboot.bean.StarterInfo;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class EditStartersDialog {
    private JPanel root;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox versionComboBox;
    private JList moduleList;
    private JList starterList;
    private JList selectList;
    private JTextField searchField;
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
        this.moduleList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                searchField.setText("");
                String name = (String) moduleList.getSelectedValue();
                starterList.setModel(new CollectionListModel(modulesMap.get(name)));
            }
        });

        // 显示详细信息
        MouseAdapter showDescAdapter = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                JList list = (JList) e.getSource();
                ListModel model = list.getModel();
                int index = list.locationToIndex(e.getPoint());
                if (index > -1) {
                    StarterInfo starter = (StarterInfo) model.getElementAt(index);
                    list.setToolTipText(starter.getDescDetails());
                }
            }
        };

        // Starter列表
        this.starterList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // 按两下选择
                    StarterInfo starterInfo = (StarterInfo) starterList.getSelectedValue();
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
                }
            }
        });
        this.starterList.addMouseMotionListener(showDescAdapter);

        // selected列表
        this.selectList.setModel(new CollectionListModel(initializr.getExistStarters()));
        this.selectList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // 按两下删除
                    StarterInfo starterInfo = (StarterInfo) selectList.getSelectedValue();
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
        this.selectList.addMouseMotionListener(showDescAdapter);

        // 搜索框
        this.searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                moduleList.clearSelection();
                String searchKey = searchField.getText().toLowerCase();
                List<StarterInfo> result = initializr.getSearchDB().entrySet().stream()
                        .filter(entry -> entry.getKey().contains(searchKey))
                        .map(Map.Entry::getValue)
                        .collect(Collectors.toList());

                starterList.setModel(new CollectionComboBoxModel(result));
            }
        });
    }

    public void show() {
        this.frame.pack();
        this.frame.setLocationRelativeTo(null); // 中间显示
        this.frame.setVisible(true);
    }
}
