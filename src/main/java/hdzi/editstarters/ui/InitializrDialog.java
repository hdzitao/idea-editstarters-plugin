package hdzi.editstarters.ui;

import com.intellij.ui.CollectionComboBoxModel;
import hdzi.editstarters.initializr.OthersHub;
import hdzi.editstarters.version.Version;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.regex.Pattern;

public class InitializrDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextField urlInput;
    private JCheckBox enableCacheCheckBox;
    private JComboBox<OthersHub> ohubComboBox;

    @Getter
    private String url;

    @Getter
    private boolean enableCache;

    @Getter
    private OthersHub othersHub;

    private final Pattern urlCheck = Pattern.compile("^https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    public InitializrDialog(String url, Version version) {
        setTitle("Spring Initializr Url");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        // url
        urlInput.setText(url);

        buttonOK.addActionListener(e -> onOK());

        // OthersHub
        String site = OthersHub.url2site(url);
        OthersHub[] othersHubs = {
                new OthersHub.GitHub(site, version),
        };
        ohubComboBox.setModel(new CollectionComboBoxModel<>(
                Arrays.asList(othersHubs),
                othersHubs[0]));

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
            this.othersHub = (OthersHub) ohubComboBox.getSelectedItem();
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
