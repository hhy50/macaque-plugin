package six.eared.macaque.plugin.idea.api;

import com.intellij.openapi.project.Project;
import six.eared.macaque.plugin.idea.common.ServerMode;
import six.eared.macaque.plugin.idea.settings.ServerConfig;

public class ServerApiFactory {

    public static ServerApi getAPI(Project project, ServerConfig serverConfig) {
        switch (serverConfig.mode) {
            case ServerMode.SERVER:
                return new RemoteApiImpl(project, serverConfig);
            case ServerMode.LOCAL:
                return LocalApiImpl.getInstance();
        }
        return null;
    }
}
