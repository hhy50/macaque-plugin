package six.eared.macaque.plugin.idea.settings;


import java.util.Objects;

public class BetaConfig extends Config {

    public static final BetaConfig EMPTY = new BetaConfig();

    /**
     * 兼容模式
     */
    public boolean compatibilityMode;


    /**
     * 远程编译
     */
    public boolean remoteCompile;

    /**
     * agent path
     */
    public String agentPath = "";

    @Override
    public boolean equals(Object o) {
        if (o instanceof BetaConfig) {
            BetaConfig to = (BetaConfig) o;
            return Objects.equals(this.compatibilityMode, to.compatibilityMode)
                    && Objects.equals(this.agentPath, to.agentPath)
                    && Objects.equals(this.remoteCompile, to.remoteCompile);
        }
        return false;
    }
}
