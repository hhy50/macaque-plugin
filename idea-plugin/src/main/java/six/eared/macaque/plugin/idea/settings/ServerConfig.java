package six.eared.macaque.plugin.idea.settings;

import org.apache.commons.lang.StringUtils;

import java.util.Objects;
import java.util.UUID;

public class ServerConfig extends Config {

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

    public String getUrl() {
        String scheme = "";
        if (serverHost != null && !serverHost.startsWith("http")) {
            scheme = "http://";
        }
        return scheme + serverHost + ":" + serverHost;
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
        return "ServerItemConfig{" +
                "serverName='" + serverName + '\'' +
                ", serverHost='" + serverHost + '\'' +
                ", sererPort='" + sererPort + '\'' +
                ", pattern='" + pattern + '\'' +
                '}';
    }
}
