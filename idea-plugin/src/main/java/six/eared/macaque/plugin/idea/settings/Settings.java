package six.eared.macaque.plugin.idea.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.Project;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import six.eared.macaque.plugin.idea.PluginInfo;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@State(name = PluginInfo.SERVER_CONFIG_ID)
public class Settings implements PersistentStateComponent<Settings.State> {

    private static final Map<Project, State> PROJECT_STATE = new HashMap<>();

    private Project project;

    public Settings(@NotNull Project project) {
        this.project = project;
    }

    public static Settings getInstance(Project project) {
        Settings settings = project.getService(Settings.class);
        if (settings.getState() == null || !settings.getState().checkRequired()) {
            return null;
        }
        return settings;
    }

    @Override
    public @Nullable State getState() {
        Settings.State state = PROJECT_STATE.get(project);
        if (state == null) {
            state = new Settings.State();
            PROJECT_STATE.put(project, state);
        }
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        cover(project, state);
    }

    public synchronized static void cover(Project project, Settings.State state) {
        PROJECT_STATE.put(project, state);
    }

    public static class State implements StateCheck {

        public List<ServerConfig> servers = new ArrayList<>();

        public BetaConfig betaConfig = BetaConfig.EMPTY;

        {
            if (CollectionUtils.isEmpty(servers)) {
                servers.add((ServerConfig) ServerConfig.DEFAULT_LOCAL.clone());
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            State to = (State) o;
            return Objects.equals(this.betaConfig, to.betaConfig)
                    && compareServerConfig(to.servers);
        }

        public ServerConfig getServerConfig(String serverUnique) {
            for (ServerConfig server : servers) {
                if (server.unique.equals(serverUnique)) {
                    return server;
                }
            }
            throw new RuntimeException("Unknown server configuration");
        }

        public boolean compareServerConfig(@NotNull List<ServerConfig> to) {
            if (this.servers == null) {
                return false;
            }

            if (servers.size() != to.size()) {
                return false;
            }

            if (CollectionUtils.isNotEmpty(CollectionUtils.removeAll(this.servers, to))) {
                return false;
            }

            Map<String, ServerConfig> toSererMap = to.stream()
                    .collect(Collectors.toMap(ServerConfig::getUnique, Function.identity()));

            for (ServerConfig server : this.servers) {
                ServerConfig serverConfig = toSererMap.get(server.getUnique());
                if (!serverConfig.equals(server)) {
                    return false;
                }
            }
            return true;
        }
    }
}
