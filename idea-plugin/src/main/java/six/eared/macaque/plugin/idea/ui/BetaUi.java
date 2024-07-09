package six.eared.macaque.plugin.idea.ui;

import com.intellij.ui.EditorTextField;
import net.miginfocom.layout.CC;
import six.eared.macaque.plugin.idea.settings.BetaConfig;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;

import static six.eared.macaque.plugin.idea.ui.UiUtil.*;

public class BetaUi extends JPanel {

    private final JPanel select = new JPanel();

    private final EditorTextField agentpathInput = new EditorTextField();

    private final JPanel agentpath = new JPanel(createMigLayout(4));

    private BetaConfig betaConfig;

    public BetaUi() {
        super(createMigLayout(4));
        this.add(this.select);
        this.add(new Label(), new CC().wrap());
        this.add(this.agentpath);
    }

    public void init(BetaConfig betaConfig) {
        this.betaConfig = betaConfig;
        addSelectBox(select, "兼容模式", (checkBox) -> {
            checkBox.setSelected(betaConfig.compatibilityMode);
            checkBox.addActionListener(event -> {
                betaConfig.compatibilityMode = checkBox.isSelected();
            });
        });
        addSelectBox(select, "远程编译", (checkBox) -> {
            checkBox.setSelected(betaConfig.remoteCompile);
            checkBox.addActionListener(event -> {
                betaConfig.remoteCompile = checkBox.isSelected();
            });
        });

        JButton download = new JButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(URI.create("https://github.com/haiyanghan/macaque-hotswap"));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        download.setText("前往下载");

        this.agentpathInput.setPreferredWidth(200);
        this.agentpathInput.setText(betaConfig.agentPath);

        this.agentpath.add(new JLabel("Agent Path"));
        this.agentpath.add(this.agentpathInput);
        this.agentpath.add(download, fillX());
    }

    public void resetUi() {
        this.select.removeAll();
        this.agentpath.removeAll();
    }

    public BetaConfig getConfig() {
        this.betaConfig.agentPath = this.agentpathInput.getText();
        return betaConfig;
    }
}
