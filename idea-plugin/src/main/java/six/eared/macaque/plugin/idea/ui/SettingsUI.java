package six.eared.macaque.plugin.idea.ui;


import org.apache.commons.collections.CollectionUtils;
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
        if (settings != null &&
                CollectionUtils.isNotEmpty(settings.getState().servers)) {
            int index = 1;
            for (ServerConfig server : settings.getState().servers) {
                this.servers.add(new ServerItemUi(index++, server));
            }
        }

        UiUtil.addGroup(panelContainer, "Servers", (inner) -> {
            for (ServerItemUi server : servers) {
                addGroup(inner, server.getName(), server, true);
            }
        });

        UiUtil.addGroup(panelContainer, "Beta", (inner) -> {
            addSelectBox(inner, "兼容模式", (checkBox) -> {
                checkBox.addChangeListener(event -> {
                    System.out.println(event.getSource());
                });
            });
        });

        // TODO 添加检查配置的按钮

        UiUtil.fillY(panelContainer);
    }

    public void initValue(Settings.State state) {
        if (state != null) {
//            serverHostTextField.setText(state.macaqueServerHost);
//            serverPortTextField.setText(state.macaqueServerPort);
//            compatibilityModeCheckBox.setSelected(state.compatibilityMode);
//            if (state.mode == 0) {
//                localModeBtn.setSelected(true);
//                remoteModeBtn.setSelected(false);
//
//                serverHostTextField.setEnabled(false);
//                serverPortTextField.setEnabled(false);
//            } else {
//                localModeBtn.setSelected(false);
//                remoteModeBtn.setSelected(true);
//            }
        }
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
}
