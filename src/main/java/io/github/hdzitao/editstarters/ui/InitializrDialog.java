package io.github.hdzitao.editstarters.ui;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.util.containers.ContainerUtil;
import io.github.hdzitao.editstarters.cache.InitializrCache;
import io.github.hdzitao.editstarters.initializr.InitializrRequest;
import io.github.hdzitao.editstarters.initializr.InitializrResponse;
import io.github.hdzitao.editstarters.ohub.OHub;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

/**
 * Initializr UI
 *
 * @version 3.2.0
 */
public class InitializrDialog implements FlowDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextField urlInput;
    private JCheckBox enableCacheCheckBox;
    private JComboBox<OHub> oHubComboBox;

    private final JFrame frame;

    private final InitializrRequest request;
    private final InitializrResponse response;

    private final static Pattern urlCheck = Pattern.compile("^https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    public InitializrDialog(InitializrRequest request, InitializrResponse response) {
        this.request = request;
        this.response = response;

        frame = new JFrame("Spring Initializr Url");
        frame.setContentPane(contentPane);
        frame.getRootPane().setDefaultButton(buttonOK);

        InitializrCache initializrCache = InitializrCache.getInstance(request.getProject());

        // url
        urlInput.setText(initializrCache.getUrl());

        buttonOK.addActionListener(e -> onOK());

        // cache 超15天默认不启动
        long updateTime = initializrCache.getUpdateTime();
        if (updateTime != 0 && System.currentTimeMillis() - updateTime > TimeUnit.DAYS.toMillis(15)) {
            enableCacheCheckBox.setSelected(false);
        }

        // OHub
        OHub[] supportedOHubs = request.getSupportedOHubs();
        if (ArrayUtils.isNotEmpty(supportedOHubs)) {
            oHubComboBox.setModel(new CollectionComboBoxModel<>(
                    Arrays.asList(supportedOHubs), ContainerUtil.find(supportedOHubs, oh ->
                    Objects.equals(initializrCache.getOHubName(), oh.getName()))));
        }

        // 点击 X 时调用 onCancel()
        frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // 遇到 ESCAPE 时调用 onCancel()
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        String url = urlInput.getText();
        if (StringUtils.isNoneBlank(url) && urlCheck.matcher(url).find()) {
            request.setUrl(url);
            request.setEnableCache(enableCacheCheckBox.isSelected());
            request.setSelectedOHub((OHub) oHubComboBox.getSelectedItem());

            frame.dispose();

            next();
        }
    }

    private void onCancel() {
        frame.dispose();
    }

    @Override
    public void show() {
        frame.pack();
        frame.setLocationRelativeTo(null); // 中间显示
        frame.setVisible(true);
    }

    @Override
    public void next() {
        // 执行初始化链
        try {
            ProgressManager progressManager = ProgressManager.getInstance();
            progressManager.runProcessWithProgressSynchronously((ThrowableComputable<Void, Exception>) () -> {
                progressManager.getProgressIndicator().setIndeterminate(true);
                request.getChain().initialize(request, response);
                return null;
            }, "Loading " + request.getUrl(), true, request.getProject());
        } catch (Exception ignore) {
            Messages.showErrorDialog("Initialization failed, please check the URL and network. If you confirm that there is no problem, please contact HdziTao.", "Initializr Chain Error");
            return;
        }

        new EditStartersDialog(request, response).show();
    }
}
