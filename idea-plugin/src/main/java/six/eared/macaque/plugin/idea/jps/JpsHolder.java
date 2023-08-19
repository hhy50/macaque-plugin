package six.eared.macaque.plugin.idea.jps;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import six.eared.macaque.plugin.idea.PluginInfo;
import six.eared.macaque.plugin.idea.api.ServerApi;
import six.eared.macaque.plugin.idea.api.ServerApiFactory;
import six.eared.macaque.plugin.idea.settings.ServerConfig;
import six.eared.macaque.plugin.idea.settings.Settings;

import java.util.*;

@State(name = PluginInfo.JPS_PROCESS_ID)
public class JpsHolder implements PersistentStateComponent<JpsHolder.State> {

    private static final Map<Project, JpsHolder.State> HOLDER =
            new HashMap<>();

    public static final int MAX_PROCESS_NAME_LENGTH = 60;

    private Project project;

    public JpsHolder(@NotNull Project project) {
        this.project = project;
    }

    public static @NotNull JpsHolder getInstance(Project project) {
        return project.getService(JpsHolder.class);
    }

    public synchronized static void refresh(Project project) {
        Settings settings = Settings.getInstance(project);
        if (settings != null) {
            List<ServerConfig> servers = settings.getState().servers;

            List<ProcessGroup> processGroups = new ArrayList<>();
            for (ServerConfig server : servers) {
                ServerApi api = ServerApiFactory.getAPI(project, server);
                processGroups.add(new ProcessGroup(server.unique, api.getJavaProcess()));
            }
            JpsHolder instance = getInstance(project);
            instance.getState().processGroups = processGroups;
        }
    }

    @Override
    public @NotNull JpsHolder.State getState() {
        State state = HOLDER.get(project);
        if (state == null) {
            state = new State();
            HOLDER.put(project, state);
        }
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        HOLDER.put(this.project, state);
    }

    public static class State {

        public List<ProcessGroup> processGroups = Collections.EMPTY_LIST;
    }

    public static class ProcessGroup {

        public String serverUnique;

        public List<ServerApi.ProcessItem> processList;

        public ProcessGroup() {

        }

        public ProcessGroup(String unique, List<ServerApi.ProcessItem> javaProcess) {
            this.serverUnique = unique;
            this.processList = javaProcess;
        }
    }
}
