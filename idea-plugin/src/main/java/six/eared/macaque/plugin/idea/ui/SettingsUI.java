package six.eared.macaque.plugin.idea.ui;


import net.miginfocom.layout.CC;
import six.eared.macaque.plugin.idea.settings.BetaConfig;
import six.eared.macaque.plugin.idea.settings.ServerConfig;
import six.eared.macaque.plugin.idea.settings.Settings;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static six.eared.macaque.plugin.idea.ui.UiUtil.*;


public class SettingsUI {

    private final JPanel panelContainer = new JPanel(createMigLayoutVertical());

    private List<ServerItemUi> servers = new ArrayList<>();

    private BetaConfig betaConfig = new BetaConfig();

    public SettingsUI(Settings settings) {
        if (settings != null) {
            int index = 1;
            for (ServerConfig server : settings.getState().servers) {
                this.servers.add(new ServerItemUi(index++, (ServerConfig) server.clone()));
            }
            this.betaConfig = (BetaConfig) settings.getState().betaConfig.clone();
        }

        UiUtil.addGroup(panelContainer, "Servers", (inner) -> {
            for (ServerItemUi server : servers) {
                inner.add(server, fillX());
                inner.add(new JPanel(), new CC().wrap());
            }
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
            ServerConfig panelConfig = server.getPanelConfig();
            panelServerConfigs.add((ServerConfig) panelConfig.clone());
        }

        Settings.State settings = new Settings.State();
        settings.servers = panelServerConfigs;
        settings.betaConfig = (BetaConfig) betaConfig.clone();
        return settings;
    }

    public void reset(Settings.State state) {

    }
}
