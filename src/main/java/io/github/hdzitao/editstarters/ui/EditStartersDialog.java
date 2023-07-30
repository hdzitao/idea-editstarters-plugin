package io.github.hdzitao.editstarters.ui;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.CollectionListModel;
import io.github.hdzitao.editstarters.buildsystem.BuildSystem;
import io.github.hdzitao.editstarters.dependency.Dependency;
import io.github.hdzitao.editstarters.dependency.Points;
import io.github.hdzitao.editstarters.dependency.Starter;
import io.github.hdzitao.editstarters.initializr.InitializrParameter;
import io.github.hdzitao.editstarters.initializr.InitializrReturn;
import io.github.hdzitao.editstarters.initializr.Module;
import io.github.hdzitao.editstarters.initializr.SpringBoot;
import io.github.hdzitao.editstarters.ui.swing.EditStartersRenderer;
import io.github.hdzitao.editstarters.ui.swing.EditStartersSelectionModel;
import io.github.hdzitao.editstarters.ui.swing.StarterListRenderer;
import io.github.hdzitao.editstarters.ui.swing.StarterListSelectionModel;
import io.github.hdzitao.editstarters.version.Version;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

/**
 * EditStarters UI
 *
 * @version 3.2.0
 */
public class EditStartersDialog {
    private JPanel root;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox<Version> versionComboBox;
    private JList<String> moduleList;
    private JList<Starter> starterList;
    private JList<Starter> selectList;
    private JTextField searchField;
    private JCheckBox cachedBox;
    private JButton removeButton;
    private JTextPane descPane;
    private final JFrame frame;
    private final Set<Starter> addStarters = new HashSet<>(64);
    private final Set<Starter> removeStarters = new HashSet<>(64);
    private final WeakHashMap<Starter, String> toolTipTextCache = new WeakHashMap<>(); // 加个缓存
    private final WeakHashMap<Starter, String> searchCache = new WeakHashMap<>(); // 搜索缓存

    public EditStartersDialog(InitializrParameter parameter, InitializrReturn ret) {


        this.frame = new JFrame("Edit Starters");
        this.frame.setContentPane(this.root);

        // 是否使用缓存
        if (ret.isEnableCache()) {
            this.cachedBox.setSelected(true);
            this.cachedBox.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    cachedBox.setToolTipText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            .format(new Date(ret.getCacheUpdateTime())));
                }
            });
        }

        // spring boot
        SpringBoot springBoot = ret.getSpringBoot();

        // boot版本选框
        this.versionComboBox.setModel(new CollectionComboBoxModel<>(
//                springBoot.getVersion().getValues().stream().map(InitializrVersion.Value::getId).collect(Collectors.toList()),
                Collections.singletonList(springBoot.getVersion()),
                springBoot.getVersion()
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

        // build system
        BuildSystem buildSystem = parameter.getBuildSystem();

        // ok按钮
        this.frame.getRootPane().setDefaultButton(this.buttonOK);
        this.buttonOK.addActionListener(e -> {
            WriteCommandAction.runWriteCommandAction(buildSystem.getContext().getData(CommonDataKeys.PROJECT), () -> {
                buildSystem.addStarters(this.addStarters);
                buildSystem.removeStarters(this.removeStarters);
            });
            this.frame.dispose();
        });

        Map<String, List<Starter>> modules = springBoot.getModules().stream()
                .collect(Collectors.toMap(Module::getName, Module::getValues, (o, n) -> o, LinkedHashMap::new));
        // Module列表
        this.moduleList.setModel(new CollectionListModel<>(modules.keySet()));
        this.moduleList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                searchField.setText("");
                String name = moduleList.getSelectedValue();
                starterList.setModel(new CollectionListModel<>(modules.getOrDefault(name, Collections.emptyList())));
//                starterList.updateUI();
            }
        });

        // 显示详细信息
        MouseAdapter showDescAdapter = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                @SuppressWarnings("unchecked")
                JList<Starter> list = (JList<Starter>) e.getSource();
                int index = list.locationToIndex(e.getPoint());
                if (index > -1) {
                    Starter starter = list.getModel().getElementAt(index);
                    list.setToolTipText(getStarterToolTipText(starter));
                }
            }
        };

        List<Dependency> existDependencies = buildSystem.getDependencies();

        // selected列表
        this.selectList.setCellRenderer(new EditStartersRenderer());
        this.selectList.setSelectionModel(new EditStartersSelectionModel());
        this.selectList.setModel(new CollectionListModel<>(modules.values().stream()
                .flatMap(List::stream)
                .filter(info -> Points.contains(existDependencies, info))
                .collect(Collectors.toList())));
        this.selectList.addMouseMotionListener(showDescAdapter);
        // 删除按钮
        this.removeButton.addActionListener(e -> {
            for (Starter Starter : selectList.getSelectedValuesList()) {
                if (Points.contains(existDependencies, Starter)) {
                    // 已存在, 需要删除
                    removeStarters.add(Starter);
                } else {
                    // 不存在,不添加
                    addStarters.remove(Starter);
                }
                // 显示
                ((CollectionListModel<Starter>) selectList.getModel()).remove(Starter);
            }
            // 清空选择
            selectList.clearSelection();
            starterList.updateUI();
        });

        // Starter列表
        this.starterList.setCellRenderer(new StarterListRenderer(this.selectList));
        this.starterList.setSelectionModel(new StarterListSelectionModel(this.starterList,
                // 选中回调
                starter -> {
                    if (Points.contains(existDependencies, starter)) {
                        // 已经存在,不删除
                        removeStarters.remove(starter);
                    } else {
                        // 不存在,需要添加
                        addStarters.add(starter);
                    }

                    CollectionListModel<Starter> selectListModel = (CollectionListModel<Starter>) selectList.getModel();
                    if (!selectListModel.contains(starter)) {
                        selectListModel.add(starter);
                    }
                },
                // 取消回调
                starter -> {
                    if (Points.contains(existDependencies, starter)) {
                        // 如果已存在,需要删除
                        removeStarters.add(starter);
                    } else {
                        // 不存在,不添加
                        addStarters.remove(starter);
                    }

                    CollectionListModel<Starter> selectListModel = (CollectionListModel<Starter>) selectList.getModel();
                    selectListModel.remove(starter);
                }));
        this.starterList.addMouseMotionListener(showDescAdapter);

        // 搜索框
        this.searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                moduleList.clearSelection();
                String searchKey = searchField.getText().toLowerCase();
                if (StringUtils.isBlank(searchKey)) {
                    starterList.setModel(new CollectionComboBoxModel<>());
                    return;
                }
                List<Starter> result = modules.values().stream().flatMap(starters -> starters.stream().filter(starter ->
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

    private String getStarterToolTipText(Starter starter) {
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
