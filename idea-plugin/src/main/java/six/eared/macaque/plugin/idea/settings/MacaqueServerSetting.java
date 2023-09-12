package six.eared.macaque.plugin.idea.settings;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import six.eared.macaque.plugin.idea.PluginInfo;
import six.eared.macaque.plugin.idea.api.ServerApiFactory;
import six.eared.macaque.plugin.idea.jps.JpsHolder;
import six.eared.macaque.plugin.idea.thread.Executors;
import six.eared.macaque.plugin.idea.ui.SettingsUI;

import javax.swing.*;

public class MacaqueServerSetting implements SearchableConfigurable, Configurable.VariableProjectAppLevel {

    private Project project;

    private SettingsUI settingsUI;

    public MacaqueServerSetting(@NotNull Project project) {
        Settings settings = Settings.getInstance(project);

        this.project = project;
        this.settingsUI = new SettingsUI();
        WriteCommandAction.runWriteCommandAction(project, () -> this.settingsUI.initValue(settings.getState()));
    }

    @Override
    public @NotNull @NonNls String getId() {
        return PluginInfo.ID;
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
        return PluginInfo.NAME;
    }

    @Override
    public @Nullable JComponent createComponent() {
        return settingsUI.showPanel();
    }

    @Override
    public boolean isModified() {
        Settings.State panelConfig = settingsUI.getPanelConfig();
        Settings settings = Settings.getInstance(project);
        if (settings != null) {
            return !settings.getState().equals(panelConfig);
        }
        return panelConfig != null;
    }

    @Override
    public void apply() {
        if (isModified()) {
            if(settingsUI.validate()){
                ServerApiFactory.clear();
                Settings.cover(project, settingsUI.getPanelConfig());
                Executors.submit(() -> JpsHolder.refresh(project));
            }else{
                Messages.showMessageDialog("Please fill the highlighted fileds","Error",Messages.getErrorIcon());
            }
        }
    }

    @Override
    public void reset() {
        if (isModified()) {
            Settings settings = Settings.getInstance(project);
            this.settingsUI.reset(settings.getState());
        }
    }

    @Override
    public boolean isProjectLevel() {
        return true;
    }
}
