package six.eared.macaque.plugin.idea.ui;


import net.miginfocom.layout.CC;
import six.eared.macaque.plugin.idea.common.ServerMode;
import six.eared.macaque.plugin.idea.settings.BetaConfig;
import six.eared.macaque.plugin.idea.settings.ServerConfig;
import six.eared.macaque.plugin.idea.settings.Settings;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static six.eared.macaque.plugin.idea.ui.UiUtil.*;


public class SettingsUI {

    private volatile int index = 1;

    private final JPanel panelContainer = new JPanel(createMigLayoutVertical());

    private List<ServerItemUi> servers = new ArrayList<>();

    private BetaConfig betaConfig = new BetaConfig();

    private JPanel serversPanel = new JPanel(createMigLayout(4));;

    public SettingsUI(Settings settings) {
        if (settings != null) {
            for (ServerConfig server : settings.getState().servers) {
                ServerItemUi serverItemUi = new ServerItemUi(this.index++, (ServerConfig) server.clone());
                this.servers.add(serverItemUi);
                this.serversPanel.add(serverItemUi, fillX());
                this.serversPanel.add(new JPanel(), new CC().wrap());
            }
            this.betaConfig = (BetaConfig) settings.getState().betaConfig.clone();
        }

        UiUtil.addGroup(panelContainer, "Servers", this.serversPanel);

        UiUtil.addButton(panelContainer, new JButton("Add"), (inner) -> {
            boolean existLocal = this.servers.stream().anyMatch(item -> item.getPanelConfig().mode.equals(ServerMode.LOCAL));
            ServerItemUi serverItemUi = new ServerItemUi(this.index++, new ServerConfig(existLocal ? ServerMode.SERVER : ServerMode.LOCAL));
            this.servers.add(serverItemUi);
            this.serversPanel.add(serverItemUi, fillX());
            this.serversPanel.add(new JPanel(), new CC().wrap());
        });

        UiUtil.addGroup(panelContainer, "Beta", (inner) -> {
            addSelectBox(inner, "兼容模式", (checkBox) -> {
                checkBox.setSelected(this.betaConfig.compatibilityMode);
                checkBox.addActionListener(event -> {
                    betaConfig.compatibilityMode = checkBox.isSelected();
                });
            });
        });

        UiUtil.fillY(panelContainer);
    }

    public JPanel showPanel() {
        return panelContainer;
    }

    public Settings.State getPanelConfig() {

        List<ServerConfig> panelServerConfigs = new ArrayList<>();
        for (ServerItemUi server : servers) {
            if (server.isDelete()) {
                continue;
            }
            ServerConfig panelConfig = server.getPanelConfig();
            panelServerConfigs.add((ServerConfig) panelConfig.clone());
        }

        Settings.State settings = new Settings.State();
        settings.servers = panelServerConfigs;
        settings.betaConfig = (BetaConfig) betaConfig.clone();
        return settings;
    }

    public void initValue() {
        for (ServerItemUi server : servers) {
            server.initValue();
        }
    }

    public void reset(Settings.State state) {

    }
}
