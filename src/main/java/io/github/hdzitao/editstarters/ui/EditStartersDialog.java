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
import io.github.hdzitao.editstarters.dependency.PointsKt;
import io.github.hdzitao.editstarters.initializr.InitializrRequest;
import io.github.hdzitao.editstarters.initializr.InitializrResponse;
import io.github.hdzitao.editstarters.springboot.Module;
import io.github.hdzitao.editstarters.springboot.SpringBoot;
import io.github.hdzitao.editstarters.springboot.Starter;
import io.github.hdzitao.editstarters.ui.swing.SelectedTableModel;
import io.github.hdzitao.editstarters.ui.swing.StarterProcessor;
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

    // 添加/删除的starters
    private final List<Dependency> existDependencies;
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

    public EditStartersDialog(InitializrRequest request, InitializrResponse response) {
        frame = new JFrame("Edit Starters");
        frame.setContentPane(root);

        // 是否使用缓存
        if (response.getEnableCache()) {
            cachedBox.setSelected(true);
            cachedBox.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    cachedBox.setToolTipText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            .format(new Date(response.getCacheUpdateTime())));
                }
            });
        }

        // 是否启用Ohub
        if (response.getEnableOHub()) {
            oHubBox.setSelected(true);
            oHubBox.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    oHubBox.setToolTipText(response.getOHub().getName());
                }
            });
        }

        // spring boot
        SpringBoot springBoot = response.getSpringBoot();

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
        BuildSystem buildSystem = request.getBuildSystem();

        // 已经存在的依赖
        existDependencies = buildSystem.getDependencies();

        // ok按钮
        frame.getRootPane().setDefaultButton(buttonOK);
        buttonOK.addActionListener(e -> {
            WriteCommandAction.runWriteCommandAction(request.getProject(), () -> {
                buildSystem.addStarters(addStarters);
                buildSystem.removeStarters(removeStarters);
            });
            frame.dispose();
        });

        // 显示详细信息
        descPane.setEditorKit(new WarpEditorKit());
        pointTextField.setBorder(JBUI.Borders.empty());
        StarterProcessor<Void> showDescListener = starter -> {
            // 详情
            descPane.setText(descPaneCache.get(starter));
            descPane.setCaretPosition(0);
            // point
            pointTextField.setText(pointPaneCache.get(starter));
            pointTextField.setCaretPosition(0);
            return null;
        };

        // 依赖信息
        Map<String, List<Starter>> modules = springBoot.getModules().stream()
                .collect(Collectors.toMap(Module::getName, Module::getValues, (o, n) -> o, LinkedHashMap::new));

        // selected/starter列表
        SelectedTableModel selectedTableModel = new SelectedTableModel(selectedList, modules.values().stream()
                .flatMap(List::stream)
                .filter(info -> PointsKt.hasPoint(existDependencies, info))
                .collect(Collectors.toList()));
        StarterTableModel starterTableModel = new StarterTableModel(starterList);
        // 添加/删除监听器
        // selected列表删除
        selectedTableModel.setRemoveProcessor(starter -> {
            removeStarter(starter);
            selectedTableModel.removeStarter(starter);
            // 刷新starter列表
            starterTableModel.fireTableDataChanged();
            return null;
        });
        // starter列表checkbox是否勾选
        starterTableModel.setCheckBoxValueProcessor(selectedTableModel::containsStarter);
        // starter列表添加
        starterTableModel.setAddProcessor(starter -> {
            addStarter(starter);
            selectedTableModel.addStarter(starter);
            return null;
        });
        // starter列表删除
        starterTableModel.setRemoveProcessor(starter -> {
            removeStarter(starter);
            selectedTableModel.removeStarter(starter);
            return null;
        });
        // 详情
        selectedTableModel.setShowDescListener(showDescListener);
        starterTableModel.setShowDescListener(showDescListener);

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
                String searchKey = searchField.getText().toLowerCase();
                if (StringUtils.isBlank(searchKey)) {
                    return;
                }
                moduleList.clearSelection();
                List<Starter> result = modules.values().stream()
                        .flatMap(Collection::stream)
                        .parallel()
                        .filter(starter -> searchCache.get(starter).contains(searchKey))
                        .collect(Collectors.toList());
                starterTableModel.refresh(result);
            }
        });
    }

    /**
     * 添加starter
     */
    private void addStarter(Starter starter) {
        if (PointsKt.hasPoint(existDependencies, starter)) {
            // 已经存在,不删除
            removeStarters.remove(starter);
        } else {
            // 不存在,需要添加
            addStarters.add(starter);
        }
    }

    /**
     * 删除starter
     */
    private void removeStarter(Starter starter) {
        if (PointsKt.hasPoint(existDependencies, starter)) {
            // 已存在, 需要删除
            removeStarters.add(starter);
        } else {
            // 不存在,不添加
            addStarters.remove(starter);
        }
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
