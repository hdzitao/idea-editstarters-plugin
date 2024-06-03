package io.github.hdzitao.editstarters.ui;

import com.intellij.concurrency.ThreadContext;
import com.intellij.openapi.application.AccessToken;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.util.containers.ContainerUtil;
import io.github.hdzitao.editstarters.cache.InitializrCache;
import io.github.hdzitao.editstarters.ohub.OHub;
import io.github.hdzitao.editstarters.version.Version;
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

/**
 * Initializr UI
 *
 * @version 3.2.0
 */
public class InitializrDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextField urlInput;
    private JCheckBox enableCacheCheckBox;
    private JComboBox<OHub> oHubComboBox;

    private String url;

    private boolean enableCache;

    private OHub oHub;

    private final static Pattern urlCheck = Pattern.compile("^https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    public InitializrDialog(InitializrCache initializrCache, Version version, OHub[] oHubs) {
        setTitle("Spring Initializr Url");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        // url
        urlInput.setText(initializrCache.getUrl());

        buttonOK.addActionListener(e -> onOK());

        // cache 超15天默认不启动
        long updateTime = initializrCache.getUpdateTime();
        if (updateTime != 0 && System.currentTimeMillis() - updateTime > TimeUnit.DAYS.toMillis(15)) {
            enableCacheCheckBox.setSelected(false);
        }

        // OHub
        if (ArrayUtils.isNotEmpty(oHubs)) {
            oHubComboBox.setModel(new CollectionComboBoxModel<>(
                    Arrays.asList(oHubs), ContainerUtil.find(oHubs, oh ->
                    Objects.equals(initializrCache.getOHubName(), oh.getName()))));
        }

        // 点击 X 时调用 onCancel()
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // 遇到 ESCAPE 时调用 onCancel()
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public String getUrl() {
        return url;
    }

    public boolean isEnableCache() {
        return enableCache;
    }

    public OHub getOHub() {
        return oHub;
    }

    private void onOK() {
        String url = urlInput.getText();
        if (StringUtils.isNoneBlank(url) && urlCheck.matcher(url).find()) {
            this.url = url;
            this.enableCache = enableCacheCheckBox.isSelected();
            this.oHub = (OHub) oHubComboBox.getSelectedItem();
            dispose();
        }
    }

    private void onCancel() {
        dispose();
    }

    public void showDialog() {
        pack();
        setLocationRelativeTo(null); // 中间显示
        try (AccessToken token = ThreadContext.resetThreadContext()) {
            setVisible(true);
        }
    }
}
