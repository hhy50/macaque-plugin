package six.eared.macaque.plugin.idea.settings;

public abstract class Config implements Cloneable {

    @Override
    public Config clone() {
        try {
            return (Config) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
