package six.eared.macaque.plugin.idea.ui;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.IdeBorderFactory;
import org.apache.commons.lang.StringUtils;
import six.eared.macaque.plugin.idea.common.ServerMode;
import six.eared.macaque.plugin.idea.settings.ServerConfig;

import javax.swing.*;
import java.awt.event.ItemEvent;

import static six.eared.macaque.plugin.idea.ui.UiUtil.*;

public class ServerItemUi extends JPanel {

    private ServerConfig serverConfig;

    private ComboBox<String> mode;

    private EditorTextField serverNameTextField;

    private EditorTextField serverHostTextField;

    private EditorTextField serverPortTextField;

    private EditorTextField processPatternTextField;

    public ServerItemUi(int index, ServerConfig serverConfig) {
        super(createMigLayout(4));
        this.setName("server-" + index);
        this.setBorder(IdeBorderFactory.createTitledBorder("server-"+index));
        this.serverConfig = serverConfig;
        this.mode = addDropdownSelectBox(this, "Mode", mode -> {
            mode.addItem(ServerMode.LOCAL);
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
    }

    public void initValue() {
        this.mode.setItem(this.serverConfig.mode);
        this.serverNameTextField.setText(this.serverConfig.serverName);
        this.serverHostTextField.setText(this.serverConfig.serverHost);
        this.serverPortTextField.setText(this.serverConfig.sererPort);
        this.processPatternTextField.setText(this.serverConfig.pattern);
    }

    private void eventChange(String seleced) {
        serverConfig.mode = seleced;
    }

    public ServerConfig getPanelConfig() {
        serverConfig.serverName = StringUtils.isEmpty(serverNameTextField.getText()) ? null : serverNameTextField.getText();
        serverConfig.serverHost = StringUtils.isEmpty(serverHostTextField.getText()) ? null : serverHostTextField.getText();
        serverConfig.sererPort = StringUtils.isEmpty(serverPortTextField.getText()) ? null : serverPortTextField.getText();
        serverConfig.pattern = StringUtils.isEmpty(processPatternTextField.getText()) ? null : processPatternTextField.getText();
        return serverConfig;
    }
}
