package io.github.hdzitao.editstarters.ui;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.SearchTextField;
import com.intellij.ui.components.JBList;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import io.github.hdzitao.editstarters.buildsystem.BuildSystem;
import io.github.hdzitao.editstarters.cache.MemoryCache;
import io.github.hdzitao.editstarters.dependency.Dependency;
import io.github.hdzitao.editstarters.dependency.Points;
import io.github.hdzitao.editstarters.initializr.InitializrParameter;
import io.github.hdzitao.editstarters.initializr.InitializrReturn;
import io.github.hdzitao.editstarters.springboot.Module;
import io.github.hdzitao.editstarters.springboot.SpringBoot;
import io.github.hdzitao.editstarters.springboot.Starter;
import io.github.hdzitao.editstarters.ui.swing.SelectedTableModel;
import io.github.hdzitao.editstarters.ui.swing.ShowDescListener;
import io.github.hdzitao.editstarters.ui.swing.StarterTableModel;
import io.github.hdzitao.editstarters.ui.swing.WarpEditorKit;
import io.github.hdzitao.editstarters.version.Version;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
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
    private JBList<String> moduleList;
    private JBTable starterList;
    private SearchTextField searchField;
    private JCheckBox cachedBox;
    private JCheckBox oHubBox;
    private JTextPane descPane;
    private JTextField pointTextField;
    private JBTable selectedList;
    private final JFrame frame;
    private final Set<Starter> addStarters = new HashSet<>(64);
    private final Set<Starter> removeStarters = new HashSet<>(64);
    // point缓存
    private final MemoryCache<Starter, String> pointPaneCache = new MemoryCache<>(starter ->
            starter.getGroupId() + " > " + starter.getArtifactId());
    // 详情缓存
    private final MemoryCache<Starter, String> descPaneCache = new MemoryCache<>(starter ->
            starter.getName() + "\n\n" + starter.getDescription());
    // 搜索缓存
    private final MemoryCache<Starter, String> searchCache = new MemoryCache<>(starter ->
            (starter.getGroupId() + ":" + starter.getArtifactId() + "\t" + starter.getName()).toLowerCase());

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
            WriteCommandAction.runWriteCommandAction(parameter.getProject(), () -> {
                buildSystem.addStarters(addStarters);
                buildSystem.removeStarters(removeStarters);
            });
            frame.dispose();
        });

        // 显示详细信息
        descPane.setEditorKit(new WarpEditorKit());
        pointTextField.setBorder(JBUI.Borders.empty());
        ShowDescListener showDescListener = starter -> {
            // 详情
            descPane.setText(descPaneCache.get(starter));
            descPane.setCaretPosition(0);
            // point
            pointTextField.setText(pointPaneCache.get(starter));
            pointTextField.setCaretPosition(0);
        };


        List<Dependency> existDependencies = buildSystem.getDependencies();
        Map<String, List<Starter>> modules = springBoot.getModules().stream()
                .collect(Collectors.toMap(Module::getName, Module::getValues, (o, n) -> o, LinkedHashMap::new));

        // selected列表
        List<Starter> selected = modules.values().stream()
                .flatMap(List::stream)
                .filter(info -> Points.contains(existDependencies, info))
                .collect(Collectors.toList());
        SelectedTableModel selectedTableModel = new SelectedTableModel(selectedList, selected)
                .setRemoveListener(starter -> {
                    if (Points.contains(existDependencies, starter)) {
                        // 已存在, 需要删除
                        removeStarters.add(starter);
                    } else {
                        // 不存在,不添加
                        addStarters.remove(starter);
                    }
                    // 显示
                    starterList.updateUI();
                })
                .setShowDescListener(showDescListener);

        // Starter列表
        StarterTableModel starterTableModel = new StarterTableModel(starterList, selectedTableModel)
                .setAddListener(starter -> {
                    if (Points.contains(existDependencies, starter)) {
                        // 已经存在,不删除
                        removeStarters.remove(starter);
                    } else {
                        // 不存在,需要添加
                        addStarters.add(starter);
                    }

                    selectedTableModel.addStarter(starter);
                })
                .setRemoveListener(starter -> {
                    if (Points.contains(existDependencies, starter)) {
                        // 如果已存在,需要删除
                        removeStarters.add(starter);
                    } else {
                        // 不存在,不添加
                        addStarters.remove(starter);
                    }

                    selectedTableModel.removeStarter(starter);
                })
                .setShowDescListener(showDescListener);

        // Module列表
        moduleList.setModel(new CollectionListModel<>(modules.keySet()));
        moduleList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                searchField.setText("");
                String name = moduleList.getSelectedValue();
                starterTableModel.refresh(modules.getOrDefault(name, Collections.emptyList()));
            }
        });

        // 搜索框
        searchField.addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                moduleList.clearSelection();
                String searchKey = searchField.getText().toLowerCase();
                if (StringUtils.isBlank(searchKey)) {
                    starterTableModel.refresh(Collections.emptyList());
                    return;
                }
                List<Starter> result = modules.values().stream()
                        .flatMap(Collection::stream)
                        .parallel()
                        .filter(starter -> searchCache.get(starter).contains(searchKey))
                        .collect(Collectors.toList());
                starterTableModel.refresh(result);
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
}
