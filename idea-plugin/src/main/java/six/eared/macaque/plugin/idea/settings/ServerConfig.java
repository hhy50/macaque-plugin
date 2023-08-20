package six.eared.macaque.plugin.idea.settings;

import org.apache.commons.lang.StringUtils;
import six.eared.macaque.plugin.idea.common.ServerMode;

import java.util.Objects;
import java.util.UUID;

public class ServerConfig extends Config {

    public static final ServerConfig DEFAULT_LOCAL = new ServerConfig("DEFAULT", ServerMode.LOCAL);

    public String unique;

    public String serverName;

    public String mode;

    public String serverHost;

    public String sererPort;

    public String pattern;

    public ServerConfig() {
        this(UUID.randomUUID().toString());
    }

    public ServerConfig(String unique) {
        this.unique = unique;
    }

    public ServerConfig(String unique, String mode) {
        this.unique = unique;
        this.mode = mode;
    }

    public String getUrl() {
        String scheme = "";
        if (serverHost != null && !serverHost.startsWith("http")) {
            scheme = "http://";
        }
        return scheme + serverHost + ":" + sererPort;
    }

    public String getUnique() {
        return this.unique;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerConfig that = (ServerConfig) o;
        return Objects.equals(unique, that.unique)
                && StringUtils.equals(mode, that.mode)
                && Objects.equals(serverName, that.serverName)
                && Objects.equals(serverHost, that.serverHost)
                && Objects.equals(sererPort, that.sererPort)
                && Objects.equals(pattern, that.pattern);
    }

    @Override
    public String toString() {
        return "ServerConfig{" +
                "unique='" + unique + '\'' +
                ", serverName='" + serverName + '\'' +
                ", mode='" + mode + '\'' +
                ", serverHost='" + serverHost + '\'' +
                ", sererPort='" + sererPort + '\'' +
                ", pattern='" + pattern + '\'' +
                '}';
    }
}
