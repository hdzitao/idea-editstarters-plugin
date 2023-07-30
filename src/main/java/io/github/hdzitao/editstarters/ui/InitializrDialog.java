package io.github.hdzitao.editstarters.ui;

import io.github.hdzitao.editstarters.version.Version;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

    @Getter
    private String url;

    @Getter
    private boolean enableCache;

    private final static Pattern urlCheck = Pattern.compile("^https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    public InitializrDialog(String url, Version version) {
        setTitle("Spring Initializr Url");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        // url
        urlInput.setText(url);

        buttonOK.addActionListener(e -> onOK());

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
