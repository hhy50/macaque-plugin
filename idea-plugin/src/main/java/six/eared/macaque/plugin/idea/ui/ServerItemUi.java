package six.eared.macaque.plugin.idea.ui;

import com.intellij.ui.IdeBorderFactory;
import net.miginfocom.layout.CC;
import org.apache.commons.lang.StringUtils;
import six.eared.macaque.plugin.idea.common.ServerMode;
import six.eared.macaque.plugin.idea.settings.ServerConfig;

import javax.swing.*;

import static six.eared.macaque.plugin.idea.ui.UiUtil.createMigLayout;
import static six.eared.macaque.plugin.idea.ui.UiUtil.fillX;

public class ServerItemUi extends JPanel {

    private volatile boolean delete = false;

    private ServerConfig serverConfig;

    private DropdownSelectBoxUi<String> mode;

    private InputUi serverName;

    private InputUi serverHost;

    private InputUi serverPort;

    private InputUi pattern;

    public ServerItemUi(int index, ServerConfig serverConfig) {
        super(createMigLayout(4));
        this.setName("server-" + index);
        this.setBorder(IdeBorderFactory.createTitledBorder("server-" + index));

        this.serverConfig = serverConfig;
        this.mode = new DropdownSelectBoxUi<>("Mode", new String[]{ServerMode.LOCAL, ServerMode.SERVER},
                this.serverConfig.mode, this::eventChange);
        this.serverName = new InputUi("Server Name");
        this.serverHost = new InputUi("Server Host");
        this.serverPort = new InputUi("Server Port");
        this.pattern = new InputUi("Pattern");

        resetUi(this.serverConfig.mode);
    }

    private void resetUi(String currentMode) {
        this.removeAll();

        this.add(this.mode, fillX());
        this.add(new JLabel(), new CC().wrap());
        this.add(this.serverName, fillX());
        this.add(new JLabel(), new CC().wrap());
        if (currentMode.equals(ServerMode.SERVER)) {
            this.add(this.serverHost, fillX());
            this.add(new JLabel(), new CC().wrap());
            this.add(this.serverPort, fillX());
            this.add(new JLabel(), new CC().wrap());
        }
        this.add(this.pattern, fillX());
        this.add(new JLabel(), new CC().wrap());
    }

    public void initValue() {
        this.serverName.setValue(this.serverConfig.serverName);
        this.serverHost.setValue(this.serverConfig.serverHost);
        this.serverPort.setValue(this.serverConfig.sererPort);
        this.pattern.setValue(this.serverConfig.pattern);
    }

    private void eventChange(String seleced) {
        switch (seleced) {
            case ServerMode.SERVER:
                break;
            case ServerMode.LOCAL:
                this.serverHost.setValue("");
                this.serverPort.setValue("");
                break;
        }
        this.resetUi(seleced);
    }

    public ServerConfig getPanelConfig() {
        this.serverConfig.mode = this.mode.getSelect();
        this.serverConfig.serverName = StringUtils.isEmpty(serverName.getValue()) ? null : serverName.getValue();
        this.serverConfig.serverHost = StringUtils.isEmpty(serverHost.getValue()) ? null : serverHost.getValue();
        this.serverConfig.sererPort = StringUtils.isEmpty(serverPort.getValue()) ? null : serverPort.getValue();
        this.serverConfig.pattern = StringUtils.isEmpty(pattern.getValue()) ? null : pattern.getValue();
        return this.serverConfig;
    }

    public boolean isDelete() {
        return delete;
    }
}
