package six.eared.macaque.plugin.idea.jps;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import six.eared.macaque.plugin.idea.PluginInfo;
import six.eared.macaque.plugin.idea.api.ServerApi;
import six.eared.macaque.plugin.idea.http.interfaces.Jps;
import six.eared.macaque.plugin.idea.notify.Notify;
import six.eared.macaque.plugin.idea.settings.Settings;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@State(name = PluginInfo.JPS_PROCESS_ID)
public class JpsHolder implements PersistentStateComponent<JpsHolder.State> {

    private static final Map<Project, JpsHolder.State> HOLDER =
            new HashMap<>();


    public static final int MAX_PROCESS_NAME_LENGTH = 60;

    private Project project;

    public JpsHolder(@NotNull Project project) {
        this.project = project;
    }

    public static JpsHolder getInstance(Project project) {
        return project.getService(JpsHolder.class);
    }

    public static void refresh(Project project) {
        JpsHolder instance = getInstance(project);
        instance.getState().processList = Collections.EMPTY_LIST;

        ServerApi.getAPI(project).setJPSList(project,instance);
    }

    @Override
    public @Nullable JpsHolder.State getState() {
        State state = HOLDER.get(project);
        if (state == null) {
            state = new State();
            HOLDER.put(project, state);
        }
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        HOLDER.put(project, state);
    }

    public static class State {
        public List<ProcessItem> processList = Collections.EMPTY_LIST;
    }

    public static class ProcessItem {
        public String pid;

        public String process;
    }
}
