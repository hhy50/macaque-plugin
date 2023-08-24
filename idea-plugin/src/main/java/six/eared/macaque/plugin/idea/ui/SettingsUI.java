package six.eared.macaque.plugin.idea.ui;

import net.miginfocom.layout.CC;
import six.eared.macaque.plugin.idea.common.ServerMode;
import six.eared.macaque.plugin.idea.settings.BetaConfig;
import six.eared.macaque.plugin.idea.settings.ServerConfig;
import six.eared.macaque.plugin.idea.settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static six.eared.macaque.plugin.idea.ui.UiUtil.*;


public class SettingsUI {

    private volatile int index = 1;

    private List<ServerItemUi> servers = new ArrayList<>();

    private BetaConfig betaConfig = null;

    private final JPanel panelContainer = new JPanel(createMigLayoutVertical());

    private JPanel serversPanel = new JPanel(createMigLayout(4));;

    private JPanel betaPanel = new JPanel(createMigLayout(4));;

    public SettingsUI() {
        UiUtil.addGroup(panelContainer, "Servers", this.serversPanel);
        UiUtil.addButton(panelContainer, new JButton("Add"), (event) -> {
            boolean existLocal = this.servers.stream().anyMatch(item -> item.getPanelConfig().mode.equals(ServerMode.LOCAL));
            ServerItemUi serverItemUi = new ServerItemUi(this.index++, new ServerConfig(existLocal ? ServerMode.SERVER : ServerMode.LOCAL));
            this.servers.add(serverItemUi);
            this.addServerConfig(serverItemUi);
        });
        UiUtil.addGroup(panelContainer, "Beta", this.betaPanel);
        UiUtil.fillY(panelContainer);
    }

    private void addServerConfig(ServerItemUi serverItemUi) {
        JPanel wrap1 = new JPanel();
        JPanel wrap2 = new JPanel();
        this.serversPanel.add(serverItemUi, fillX());
        this.serversPanel.add(wrap1, new CC().wrap());
        this.serversPanel.add(creatRemoveBtn(serverItemUi, wrap1, wrap2));
        this.serversPanel.add(wrap2, new CC().wrap());
    }

    private Component creatRemoveBtn(ServerItemUi serverItemUi, Component... wraps) {
        JButton remove = new JButton("Remove");
        remove.addActionListener(e -> {
            this.servers.remove(serverItemUi);
            this.serversPanel.remove(serverItemUi);
            for (Component wrap : wraps) {
                this.serversPanel.remove(wrap);
            }
            this.serversPanel.remove(remove);
        });
        return remove;
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

    public void initValue(Settings.State state) {
        for (ServerConfig server : state.servers) {
            ServerItemUi serverItemUi = new ServerItemUi(this.index++, (ServerConfig) server.clone());
            serverItemUi.initValue();
            this.servers.add(serverItemUi);
            this.addServerConfig(serverItemUi);
        }
        this.betaConfig = (BetaConfig) state.betaConfig.clone();
        addSelectBox(this.betaPanel, "兼容模式", (checkBox) -> {
            checkBox.setSelected(this.betaConfig.compatibilityMode);
            checkBox.addActionListener(event -> {
                this.betaConfig.compatibilityMode = checkBox.isSelected();
            });
        });
    }

    public void reset(Settings.State state) {
        this.servers.clear();
        this.serversPanel.removeAll();
        this.betaPanel.removeAll();
        initValue(state);
    }
}
