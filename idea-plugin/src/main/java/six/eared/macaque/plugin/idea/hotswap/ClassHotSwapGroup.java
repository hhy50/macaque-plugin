package six.eared.macaque.plugin.idea.hotswap;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import six.eared.macaque.plugin.idea.api.ServerApi;
import six.eared.macaque.plugin.idea.jps.JpsHolder;

import java.util.ArrayList;
import java.util.List;


public class ClassHotSwapGroup extends ActionGroup {

    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent event) {
        Project project = event.getProject();
        JpsHolder instance = JpsHolder.getInstance(project);

        List<AnAction> actions = new ArrayList<>();
        for (JpsHolder.ProcessGroup processGroup : instance.getState().processGroups) {
            for (ServerApi.ProcessItem processItem : processGroup.processList) {
                actions.add(new ClassHotSwapAction(processGroup.serverUnique, processItem.pid, processItem.process));
            }
            actions.add(new Separator());
        }
        actions.add(new RefreshJavaProcessAction());
        return actions.toArray(new AnAction[0]);
    }
}
