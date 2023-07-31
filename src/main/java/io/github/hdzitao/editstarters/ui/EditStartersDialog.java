package io.github.hdzitao.editstarters.ui;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.CollectionListModel;
import com.intellij.util.ui.JBUI;
import io.github.hdzitao.editstarters.buildsystem.BuildSystem;
import io.github.hdzitao.editstarters.dependency.Dependency;
import io.github.hdzitao.editstarters.dependency.Points;
import io.github.hdzitao.editstarters.dependency.Starter;
import io.github.hdzitao.editstarters.initializr.InitializrParameter;
import io.github.hdzitao.editstarters.initializr.InitializrReturn;
import io.github.hdzitao.editstarters.springboot.Module;
import io.github.hdzitao.editstarters.springboot.SpringBoot;
import io.github.hdzitao.editstarters.ui.swing.*;
import io.github.hdzitao.editstarters.version.Version;
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
    private JCheckBox oHubBox;
    private JTextPane descPane;
    private JTextField pointTextField;
    private final JFrame frame;
    private final Set<Starter> addStarters = new HashSet<>(64);
    private final Set<Starter> removeStarters = new HashSet<>(64);
    private final WeakHashMap<Starter, String> pointPaneCache = new WeakHashMap<>(); // point缓存
    private final WeakHashMap<Starter, String> descPaneCache = new WeakHashMap<>(); // 详情缓存
    private final WeakHashMap<Starter, String> searchCache = new WeakHashMap<>(); // 搜索缓存

    public EditStartersDialog(InitializrParameter parameter, InitializrReturn ret) {
        frame = new JFrame("Edit Starters");
        frame.setContentPane(root);

        // 是否使用缓存
        if (ret.isEnableCache()) {
            cachedBox.setSelected(true);
            cachedBox.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    cachedBox.setToolTipText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            .format(new Date(ret.getCacheUpdateTime())));
                }
            });
        }

        // 是否启用Ohub
        if (ret.isEnableOHub()) {
            oHubBox.setSelected(true);
            oHubBox.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    oHubBox.setToolTipText(ret.getOHub().getName());
                }
            });
        }

        // spring boot
        SpringBoot springBoot = ret.getSpringBoot();

        // boot版本选框
        versionComboBox.setModel(new CollectionComboBoxModel<>(
//                springBoot.getVersion().getValues().stream().map(InitializrVersion.Value::getId).collect(Collectors.toList()),
                Collections.singletonList(springBoot.getVersion()),
                springBoot.getVersion()
        ));
        versionComboBox.setEnabled(false);

        // 取消按钮
        buttonCancel.addActionListener(e -> onCancel());
        // 点击 X 时调用 onCancel()
        frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        // 遇到 ESCAPE 时调用 onCancel()
        root.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // build system
        BuildSystem buildSystem = parameter.getBuildSystem();

        // ok按钮
        frame.getRootPane().setDefaultButton(buttonOK);
        buttonOK.addActionListener(e -> {
            WriteCommandAction.runWriteCommandAction(buildSystem.getContext().getData(CommonDataKeys.PROJECT), () -> {
                buildSystem.addStarters(addStarters);
                buildSystem.removeStarters(removeStarters);
            });
            frame.dispose();
        });

        Map<String, List<Starter>> modules = springBoot.getModules().stream()
                .collect(Collectors.toMap(Module::getName, Module::getValues, (o, n) -> o, LinkedHashMap::new));
        // Module列表
        moduleList.setModel(new CollectionListModel<>(modules.keySet()));
        moduleList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                searchField.setText("");
                String name = moduleList.getSelectedValue();
                starterList.setModel(new CollectionListModel<>(modules.getOrDefault(name, Collections.emptyList())));
//                starterList.updateUI();
            }
        });

        // 显示详细信息
        descPane.setEditorKit(new WarpEditorKit());
        pointTextField.setBorder(JBUI.Borders.empty());
        MouseAdapter showDescAdapter = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                @SuppressWarnings("unchecked")
                JList<Starter> list = (JList<Starter>) e.getSource();
                int index = list.locationToIndex(e.getPoint());
                if (index > -1) {
                    Starter starter = list.getModel().getElementAt(index);
                    // 详情
                    descPane.setText(getStarterDesc(starter));
                    descPane.setCaretPosition(0);
                    // point
                    pointTextField.setText(getPointText(starter));
                    pointTextField.setCaretPosition(0);
                }
            }
        };

        List<Dependency> existDependencies = buildSystem.getDependencies();

        // selected列表
        selectList.setCellRenderer(new EditStartersRenderer());
        selectList.setSelectionModel(new EditStartersSelectionModel());
        selectList.setModel(new CollectionListModel<>(modules.values().stream()
                .flatMap(List::stream)
                .filter(info -> Points.contains(existDependencies, info))
                .collect(Collectors.toList())));
        selectList.addMouseMotionListener(showDescAdapter);
        selectList.addMouseListener(showDescAdapter);
        // 删除按钮
        removeButton.addActionListener(e -> {
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
        starterList.setCellRenderer(new StarterListRenderer(selectList));
        starterList.setSelectionModel(new StarterListSelectionModel(starterList,
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
        starterList.addMouseMotionListener(showDescAdapter);
        starterList.addMouseListener(showDescAdapter);

        // 搜索框
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                moduleList.clearSelection();
                String searchKey = searchField.getText().toLowerCase();
                if (StringUtils.isBlank(searchKey)) {
                    starterList.setModel(new CollectionComboBoxModel<>());
                    return;
                }
                List<Starter> result = modules.values().stream()
                        .flatMap(Collection::stream)
                        .parallel()
                        .filter(starter -> getSearchText(starter).contains(searchKey))
                        .collect(Collectors.toList());
                starterList.setModel(new CollectionComboBoxModel<>(result));
            }
        });
    }

    private void onCancel() {
        frame.dispose();
    }

    public void show() {
        frame.pack();
        frame.setLocationRelativeTo(null); // 中间显示
        frame.setVisible(true);
    }

    private String getStarterDesc(Starter starter) {
        return descPaneCache.computeIfAbsent(starter, info ->
                info.getName() + "\n\n" + info.getDescription());
    }

    private String getSearchText(Starter starter) {
        return searchCache.computeIfAbsent(starter, info ->
                (info.getGroupId() + ":" + info.getArtifactId() + "\t" + info.getName()).toLowerCase());
    }

    private String getPointText(Starter starter) {
        return pointPaneCache.computeIfAbsent(starter, info ->
                info.getGroupId() + " > " + info.getArtifactId());
    }
}
