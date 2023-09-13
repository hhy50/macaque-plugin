package six.eared.macaque.plugin.idea.ui;

import com.intellij.openapi.ui.ComboBox;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

import static six.eared.macaque.plugin.idea.ui.UiUtil.*;

public class DropdownSelectBoxUi<T> extends JPanel {

    private ComboBox<T> comboBox;
    private JLabel errMsgLabel = null;
    public DropdownSelectBoxUi(String name, T[] values, T defaultItem, Consumer<T> consumer) {
        super(createMigLayout());
        this.comboBox = new ComboBox<>(values);
        this.comboBox.setItem(defaultItem);
        this.comboBox.addItemListener(item -> {
            consumer.accept((T) item.getItem());
        });

        this.add(createEqualWidthLabel(name));
        this.add(this.comboBox, fillX());

        errMsgLabel = new JLabel("");
        errMsgLabel.setPreferredSize(new Dimension(100, 30));
        errMsgLabel.setForeground(Color.red);
        this.add(errMsgLabel);

    }

    public T getSelect() {
        return this.comboBox.getItem();
    }
}
