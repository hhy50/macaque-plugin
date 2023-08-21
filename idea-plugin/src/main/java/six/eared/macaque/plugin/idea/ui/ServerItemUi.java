package six.eared.macaque.plugin.idea.ui;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.IdeBorderFactory;
import org.apache.commons.lang.StringUtils;
import six.eared.macaque.plugin.idea.common.ServerMode;
import six.eared.macaque.plugin.idea.settings.ServerConfig;

import javax.swing.*;
import java.awt.*;

import static six.eared.macaque.plugin.idea.ui.UiUtil.*;

public class ServerItemUi extends JPanel {

    private volatile boolean delete = false;

    private ServerConfig serverConfig;

    private ComboBox<String> mode;

    private EditorTextField serverNameTextField;

    private EditorTextField serverHostTextField;

    private EditorTextField serverPortTextField;

    private EditorTextField processPatternTextField;

    public ServerItemUi(int index, ServerConfig serverConfig) {
        super(createMigLayout(4));
        this.setName("server-" + index);
        this.setBorder(IdeBorderFactory.createTitledBorder("server-" + index));

        this.serverConfig = serverConfig;
        this.mode = addDropdownSelectBox(this, "Mode", createDropdownSelectBox());
        this.serverNameTextField = addInputBox(this, "Server Name");
        this.serverHostTextField = addInputBox(this, "Server Host");
        this.serverPortTextField = addInputBox(this, "Server Port");
        this.processPatternTextField = addInputBox(this, "Pattern");
        this.add(creatRemoveBtn());
    }

    public void initValue() {
        this.serverNameTextField.setText(this.serverConfig.serverName);
        this.serverHostTextField.setText(this.serverConfig.serverHost);
        this.serverPortTextField.setText(this.serverConfig.sererPort);
        this.processPatternTextField.setText(this.serverConfig.pattern);
    }

    private void eventChange() {
        serverConfig.mode = this.mode.getItem();
    }

    public ServerConfig getPanelConfig() {
        serverConfig.serverName = StringUtils.isEmpty(serverNameTextField.getText()) ? null : serverNameTextField.getText();
        serverConfig.serverHost = StringUtils.isEmpty(serverHostTextField.getText()) ? null : serverHostTextField.getText();
        serverConfig.sererPort = StringUtils.isEmpty(serverPortTextField.getText()) ? null : serverPortTextField.getText();
        serverConfig.pattern = StringUtils.isEmpty(processPatternTextField.getText()) ? null : processPatternTextField.getText();
        return serverConfig;
    }

    private Component creatRemoveBtn() {
        ServerItemUi that = this;

        JButton remove = new JButton("Remove");
        remove.addActionListener(e -> {
            that.delete = true;
            final Container parent = that.getParent();
            parent.remove(that);
        });
        Panel panel = new Panel();
        panel.add(remove, BorderLayout.EAST);
        return panel;
    }

    private ComboBox<String> createDropdownSelectBox() {
        ComboBox<String> comboBox = new ComboBox<>(new String[]{ServerMode.LOCAL, ServerMode.SERVER});
        comboBox.setItem(this.serverConfig.mode);
        comboBox.addActionListener(e -> {
            eventChange();
        });
        return comboBox;
    }

    public boolean isDelete() {
        return delete;
    }
}
