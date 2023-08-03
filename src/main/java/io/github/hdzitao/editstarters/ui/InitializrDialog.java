package io.github.hdzitao.editstarters.ui;

import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.util.containers.ContainerUtil;
import io.github.hdzitao.editstarters.cache.InitializrCache;
import io.github.hdzitao.editstarters.ohub.OHub;
import io.github.hdzitao.editstarters.version.Version;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Objects;
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

    @Getter
    private String url;

    @Getter
    private boolean enableCache;

    @Getter
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
        setVisible(true);
    }
}
