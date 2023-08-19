package six.eared.macaque.plugin.idea.ui;

import com.intellij.ui.EditorTextField;
import org.apache.commons.lang.StringUtils;
import six.eared.macaque.plugin.idea.common.ServerMode;
import six.eared.macaque.plugin.idea.settings.ServerConfig;

import javax.swing.*;
import java.awt.event.ItemEvent;

import static six.eared.macaque.plugin.idea.ui.UiUtil.*;

public class ServerItemUi extends JPanel {

    private String unique;

    private boolean serverMode;

    private EditorTextField serverNameTextField;

    private EditorTextField serverHostTextField;

    private EditorTextField serverPortTextField;

    private EditorTextField processPatternTextField;

    public ServerItemUi(int index, ServerConfig initConfig) {
        super(createMigLayout(4));

        this.setName("server-" + index);
        addDropdownSelectBox(this, "Mode", mode -> {
//            mode.addItem(ServerMode.LOCAL);
            mode.addItem(ServerMode.SERVER);
            mode.addItemListener(event -> {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    eventChange((String) event.getItem());
                }
            });
        });
        this.serverNameTextField = addInputBox(this, "Server Name");
        this.serverHostTextField = addInputBox(this, "Server Host");
        this.serverPortTextField = addInputBox(this, "Server Port");
        this.processPatternTextField = addInputBox(this, "Pattern");

        initValue(initConfig);
    }

    private void initValue(ServerConfig value) {
        this.unique = value.unique;
        this.serverNameTextField.setText(value.serverName);
        this.serverHostTextField.setText(value.serverHost);
        this.serverPortTextField.setText(value.sererPort);
        this.processPatternTextField.setText(value.pattern);
    }

    private void eventChange(String seleced) {
        this.serverMode = seleced.equals(ServerMode.SERVER);
        if (this.serverMode) {

        } else {
            this.remove(this.serverHostTextField);
            this.remove(this.serverPortTextField);
        }
    }

    public ServerConfig getPanelConfig() {
        ServerConfig panelConfig = new ServerConfig(unique);
        panelConfig.mode = serverMode ? ServerMode.SERVER : ServerMode.LOCAL;
        panelConfig.serverName = StringUtils.isEmpty(serverNameTextField.getText()) ? null : serverNameTextField.getText();
        panelConfig.serverHost = StringUtils.isEmpty(serverHostTextField.getText()) ? null : serverHostTextField.getText();
        panelConfig.sererPort = StringUtils.isEmpty(serverPortTextField.getText()) ? null : serverPortTextField.getText();
        panelConfig.pattern = StringUtils.isEmpty(processPatternTextField.getText()) ? null : processPatternTextField.getText();
        return panelConfig;
    }
}
