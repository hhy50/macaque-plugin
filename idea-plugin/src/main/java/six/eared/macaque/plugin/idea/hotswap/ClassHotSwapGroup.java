package six.eared.macaque.plugin.idea.hotswap;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.project.Project;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import six.eared.macaque.plugin.idea.api.ServerApi;
import six.eared.macaque.plugin.idea.api.ServerApiFactory;
import six.eared.macaque.plugin.idea.jps.JpsHolder;
import six.eared.macaque.plugin.idea.settings.Settings;

import java.util.ArrayList;
import java.util.List;


public class ClassHotSwapGroup extends ActionGroup {

    private static final Separator SEPARATOR = new Separator();
    private static final NotOptionalAction notOptionalAction = new NotOptionalAction("<no java process>");

    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent event) {
        Project project = event.getProject();

        List<AnAction> actions = new ArrayList<>();
        Settings settings = Settings.getInstance(project);
        if (settings != null) {
            JpsHolder jpsHolder = JpsHolder.getInstance(project);
            for (JpsHolder.ProcessGroup processGroup : jpsHolder.getState().processGroups) {
                ServerApi api = ServerApiFactory.getAPI(project, settings.getState().getServerConfig(processGroup.serverUnique));

                actions.add(new NotOptionalAction(processGroup.serverName));
                if (CollectionUtils.isNotEmpty(processGroup.processList)) {
                    for (ServerApi.ProcessItem processItem : processGroup.processList) {
                        actions.add(new ClassHotSwapAction(api, processItem.pid, processItem.process));
                    }
                } else {
                    actions.add(notOptionalAction);
                }
                actions.add(SEPARATOR);
            }
            actions.add(new RefreshJavaProcessAction());
        }
        return actions.toArray(new AnAction[0]);
    }

    static class NotOptionalAction extends AnAction {
        public NotOptionalAction(String name) {
            super(name);
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            e.getPresentation().setEnabled(false);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {

        }
    }
}
