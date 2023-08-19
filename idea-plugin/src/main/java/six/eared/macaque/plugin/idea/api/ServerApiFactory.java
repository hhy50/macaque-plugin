package six.eared.macaque.plugin.idea.api;

import com.intellij.openapi.project.Project;
import six.eared.macaque.plugin.idea.common.ServerMode;
import six.eared.macaque.plugin.idea.settings.ServerConfig;

import java.util.HashMap;
import java.util.Map;

public class ServerApiFactory {

    private static final Map<String, ServerApi> cache = new HashMap<>();

    public static ServerApi getAPI(Project project, ServerConfig serverConfig) {
        ServerApi serverApi = cache.get(serverConfig.unique);
        if (serverApi != null) {
            if (serverApi instanceof RemoteApiImpl
                    && !serverConfig.mode.equals(ServerMode.SERVER)) {
                serverApi = null;
            }
            if (serverApi instanceof LocalApiImpl
                    && !serverConfig.mode.equals(ServerMode.LOCAL)) {
                serverApi = null;
            }
        }
        if (serverApi == null) {
            serverApi = createApi(project, serverConfig);
            cache.put(serverConfig.unique, serverApi);
        }
        return serverApi;
    }


    public static ServerApi createApi(Project project, ServerConfig serverConfig) {
        switch (serverConfig.mode) {
            case ServerMode.SERVER:
                return new RemoteApiImpl(project, serverConfig);
            case ServerMode.LOCAL:
                return new LocalApiImpl(serverConfig);
        }
        return null;
    }
}
