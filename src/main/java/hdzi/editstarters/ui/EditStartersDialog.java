package hdzi.editstarters.ui;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.CollectionListModel;
import hdzi.editstarters.buildsystem.BuildSystem;
import hdzi.editstarters.dependency.Module;
import hdzi.editstarters.dependency.*;
import hdzi.editstarters.initializr.InitializrStatus;
import hdzi.editstarters.version.Version;
import org.apache.commons.lang.WordUtils;

import javax.swing.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

public class EditStartersDialog {
    private JPanel root;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox<Version> versionComboBox;
    private JList<String> moduleList;
    private JList<StarterInfo> starterList;
    private JList<StarterInfo> selectList;
    private JTextField searchField;
    private JCheckBox cachedBox;
    private JCheckBox ohubBox;
    private final JFrame frame;
    private final Set<StarterInfo> addStarters = new HashSet<>(64);
    private final Set<StarterInfo> removeStarters = new HashSet<>(64);
    private final WeakHashMap<StarterInfo, String> toolTipTextCache = new WeakHashMap<>(); // 加个缓存
    private final WeakHashMap<StarterInfo, String> searchCache = new WeakHashMap<>(); // 搜索缓存

    public EditStartersDialog(BuildSystem buildSystem, SpringBoot springBoot, InitializrStatus status) {
        this.frame = new JFrame("Edit Starters");
        this.frame.setContentPane(this.root);

        // 是否使用缓存
        if (status.isEnableCache()) {
            this.cachedBox.setSelected(true);
            this.cachedBox.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    cachedBox.setToolTipText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            .format(new Date(status.getCacheUpdateTime())));
                }
            });
        }

        // 是否启用Ohub
        if (status.isEnableOHub()) {
            this.ohubBox.setSelected(true);
            this.ohubBox.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    ohubBox.setToolTipText(status.getOHubName());
                }
            });
        }

        // boot版本选框
        this.versionComboBox.setModel(new CollectionComboBoxModel<>(
//                springBoot.getVersion().getValues().stream().map(InitializrVersion.Value::getId).collect(Collectors.toList()),
                Collections.singletonList(springBoot.getBootVersion()),
                springBoot.getBootVersion()
        ));
        this.versionComboBox.setEnabled(false);

        // 取消按钮
        this.buttonCancel.addActionListener(e -> onCancel());
        // 点击 X 时调用 onCancel()
        this.frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        // 遇到 ESCAPE 时调用 onCancel()
        this.root.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        // ok按钮
        this.frame.getRootPane().setDefaultButton(this.buttonOK);
        this.buttonOK.addActionListener(e -> {
            WriteCommandAction.runWriteCommandAction(buildSystem.getContext().getData(CommonDataKeys.PROJECT), () -> {
                buildSystem.addStarters(this.addStarters);
                buildSystem.removeStarters(this.removeStarters);
            });
            this.frame.dispose();
        });

        Map<String, List<StarterInfo>> modules = springBoot.getModules().stream()
                .collect(Collectors.toMap(Module::getName, Module::getValues, (o, n) -> o, LinkedHashMap::new));
        // Module列表
        this.moduleList.setModel(new CollectionListModel<>(modules.keySet()));
        this.moduleList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                searchField.setText("");
                String name = moduleList.getSelectedValue();
                starterList.setModel(new CollectionListModel<>(modules.getOrDefault(name, Collections.emptyList())));
            }
        });

        // 显示详细信息
        MouseAdapter showDescAdapter = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                @SuppressWarnings("unchecked")
                JList<StarterInfo> list = (JList<StarterInfo>) e.getSource();
                int index = list.locationToIndex(e.getPoint());
                if (index > -1) {
                    StarterInfo starter = list.getModel().getElementAt(index);
                    list.setToolTipText(getStarterInfoToolTipText(starter));
                }
            }
        };

        List<Dependency> existDependencies = buildSystem.getDependencies();
        // Starter列表
        this.starterList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                StarterInfo starterInfo = starterList.getSelectedValue();
                if (Points.contains(existDependencies, starterInfo)) { // 对于已存在的starter，添加就是从删除列表里删除
                    removeStarters.remove(starterInfo);
                } else { // 对于不存在的starter，添加直接加入添加列表
                    addStarters.add(starterInfo);
                }
                // 去重显示
                CollectionListModel<StarterInfo> listModel = (CollectionListModel<StarterInfo>) selectList.getModel();
                if (!listModel.contains(starterInfo)) {
                    listModel.add(starterInfo);
                }
            }
        });
        this.starterList.addMouseMotionListener(showDescAdapter);

        // selected列表
        List<StarterInfo> existStarters = modules.values().stream()
                .flatMap(List::stream)
                .filter(info -> Points.contains(existDependencies, info))
                .collect(Collectors.toList());
        this.selectList.setModel(new CollectionListModel<>(existStarters));
        this.selectList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                StarterInfo starterInfo = selectList.getSelectedValue();
                if (Points.contains(existDependencies, starterInfo)) { // 对于已存在的starter，删除就是加入删除列表
                    removeStarters.add(starterInfo);
                } else { // 对于不存在的starter，删除是从添加列表里删除
                    addStarters.remove(starterInfo);
                }
                // 显示
                ((CollectionListModel<StarterInfo>) selectList.getModel()).remove(starterInfo);
            }
        });
        this.selectList.addMouseMotionListener(showDescAdapter);

        // 搜索框
        this.searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                moduleList.clearSelection();
                String searchKey = searchField.getText().toLowerCase();
                List<StarterInfo> result = modules.values().stream().flatMap(starters -> starters.stream().filter(starter ->
                                searchCache.computeIfAbsent(starter, key -> (key.getGroupId() + ":" + key.getArtifactId() + "\t" + key.getName()).toLowerCase())
                                        .contains(searchKey)))
                        .collect(Collectors.toList());
                starterList.setModel(new CollectionComboBoxModel<>(result));
            }
        });
    }

    private void onCancel() {
        this.frame.dispose();
    }

    public void show() {
        this.frame.pack();
        this.frame.setLocationRelativeTo(null); // 中间显示
        this.frame.setVisible(true);
    }

    private String getStarterInfoToolTipText(StarterInfo starter) {
        return toolTipTextCache.computeIfAbsent(starter, info -> {
            StringBuilder buffer = new StringBuilder();
            buffer.append("Name: ").append(info.getName()).append("<br/>")
                    .append("GroupId: ").append(info.getGroupId()).append("<br/>")
                    .append("ArtifactId: ").append(info.getArtifactId()).append("<br/>")
                    .append("Scope: ").append(info.getScope()).append("<br/>");
            if (info.getVersion() != null) {
                buffer.append("Version: ").append(info.getVersion()).append("<br/>");
            }
            if (info.getVersionRange() != null) {
                buffer.append("Version Range: ").append(info.getVersionRange()).append("<br/>");
            }
            buffer.append("<br/>").append(WordUtils.wrap(info.getDescription(), 50, "<br/>", false));

            return buffer.toString();
        });
    }
}
