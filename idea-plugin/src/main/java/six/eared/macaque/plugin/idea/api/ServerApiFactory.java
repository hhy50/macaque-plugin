package six.eared.macaque.plugin.idea.api;

import com.intellij.openapi.project.Project;
import six.eared.macaque.plugin.idea.common.ServerMode;
import six.eared.macaque.plugin.idea.settings.ServerConfig;
import six.eared.macaque.plugin.idea.settings.Settings;

import java.util.HashMap;
import java.util.Map;

public class ServerApiFactory {

    private static final Map<String, ServerApi> cache = new HashMap<>();

    public static ServerApi getAPI(Project project, String serverUnique) {
        ServerApi serverApi = cache.get(serverUnique);
        if (serverApi == null) {
            serverApi = createApi(project, serverUnique);
            cache.put(serverUnique, serverApi);
        }
        return serverApi;
    }


    public static ServerApi createApi(Project project, String serverUnique) {
        ServerConfig serverConfig = Settings.getInstance(project).getState().getServerConfig(serverUnique);
        switch (serverConfig.mode) {
            case ServerMode.SERVER:
                return new RemoteApiImpl(project, serverUnique);
            case ServerMode.LOCAL:
                return new LocalApiImpl(project, serverUnique);
        }
        return null;
    }
}
