package six.eared.macaque.plugin.idea.settings;


import java.util.Objects;

public class BetaConfig extends Config {

    public static final BetaConfig EMPTY = new BetaConfig();

    /**
     * 兼容模式
     */
    public boolean compatibilityMode;

    @Override
    public boolean equals(Object o) {
        if (o instanceof BetaConfig) {
            BetaConfig to = (BetaConfig) o;
            return Objects.equals(this.compatibilityMode, to.compatibilityMode);
        }
        return false;
    }
}
