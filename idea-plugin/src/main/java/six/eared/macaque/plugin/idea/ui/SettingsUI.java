package six.eared.macaque.plugin.idea.ui;


import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBRadioButton;
import org.apache.commons.lang.StringUtils;
import six.eared.macaque.plugin.idea.settings.Settings;

import javax.swing.*;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import static six.eared.macaque.plugin.idea.ui.UiUtil.*;


public class SettingsUI {

    private final JPanel panelContainer = new JPanel(createMigLayoutVertical());

    private EditorTextField serverHostTextField;

    private EditorTextField serverPortTextField;

    private JBCheckBox compatibilityModeCheckBox;

    private EditorTextField processFilterTextField;

    private JBRadioButton localModeBtn;
    private JBRadioButton remoteModeBtn;
    /**
     * 模式
     * 0 本地模式
     * 1 远程模式
     */
    private int apiMode;
    public SettingsUI() {
        UiUtil.addGroup(panelContainer, "Main", (inner) -> {
            JBRadioButton [] btns = addModeChangeRadio(inner);
            localModeBtn = btns[0];
            remoteModeBtn = btns[1];
            serverHostTextField = addInputBox(inner, "Server Host");
            serverPortTextField = addInputBox(inner, "Server Port");
            processFilterTextField = addInputBox(inner, "Process Filter");
        });

        UiUtil.addGroup(panelContainer, "Beta", (inner) -> {
            compatibilityModeCheckBox = addSelectBox(inner, "兼容模式");
        });
        remoteModeBtn.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                serverHostTextField.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
                serverPortTextField.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
                apiMode = e.getStateChange() == ItemEvent.SELECTED?1:0;
            }
        });
        // TODO 添加检查配置的按钮
        UiUtil.fillY(panelContainer);
    }

    public void initValue(Settings.State state) {
        if (state != null) {
            serverHostTextField.setText(state.macaqueServerHost);
            serverPortTextField.setText(state.macaqueServerPort);
            compatibilityModeCheckBox.setSelected(state.compatibilityMode);
            if(state.mode==0){
                localModeBtn.setSelected(true);
                remoteModeBtn.setSelected(false);

                serverHostTextField.setEnabled(false);
                serverPortTextField.setEnabled(false);
            }else{
                localModeBtn.setSelected(false);
                remoteModeBtn.setSelected(true);
            }
        }
    }

    public JPanel showPanel() {
        return panelContainer;
    }

    public Settings.State getPanelConfig() {
        String hostText = serverHostTextField.getText();
        String portText = serverPortTextField.getText();
        String processFilter = processFilterTextField.getText();
        boolean selected = compatibilityModeCheckBox.isSelected();
        Settings.State settings = new Settings.State();
        settings.macaqueServerHost = hostText;
        settings.macaqueServerPort = portText;
        settings.compatibilityMode = selected;
        settings.processFilter  = processFilter;
        settings.mode = this.apiMode;
        return settings;
    }
}
