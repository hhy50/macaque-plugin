package six.eared.macaque.plugin.idea.ui;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBCheckBox;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class UiUtil {

    /**
     * addGroup
     *
     * @param container
     * @param groupName
     * @param custom
     */
    public static void addGroup(JPanel container, String groupName, Consumer<JPanel> custom) {
        JPanel inner = new JPanel(createMigLayout(4));
        custom.accept(inner);

        addGroup(container, groupName, inner);
    }

    public static void addGroup(JPanel container, String groupName, JPanel inner) {
        JPanel group = new JPanel(createMigLayout());
        group.setBorder(IdeBorderFactory.createTitledBorder(groupName));
        group.add(inner, fillX());

        container.add(group, fillX());
    }

    /**
     * 增加输入框
     *
     * @param container
     */
    public static EditorTextField addInputBox(JPanel container, String labelName) {
        JPanel inner = new JPanel(createMigLayout());

        JLabel jLabel = new JLabel(labelName);
        jLabel.setPreferredSize(new Dimension(100, 30));

        inner.add(jLabel);
        inner.add(new EditorTextField(), fillX());

        container.add(inner, fillX());
        container.add(new JLabel(), new CC().wrap());
        return new EditorTextField();
    }

    public static CC fillX() {
        return new CC().growX().pushX();
    }

    public static void fillY(JPanel container) {
        container.add(new JPanel(), new CC().growY().pushY());
    }

    public static JBCheckBox addSelectBox(JPanel container, String selectName, Consumer<JBCheckBox> consumer) {
        JBCheckBox checkBox = new JBCheckBox(selectName);
        consumer.accept(checkBox);

        container.add(checkBox, new CC().wrap());
        return checkBox;
    }

    public static MigLayout createMigLayout() {
        return createMigLayout("0!", "0!", "0");
    }

    public static MigLayout createMigLayout(int gapx) {
        return createMigLayout(gapx + "px",
                "0!", "0");
    }

    public static MigLayout createMigLayout(String gapx, String gapy, String inset) {
        LC lc = new LC();
        lc.fill();
        lc.gridGap(gapx, gapy)
                .insets(inset);

        return new MigLayout(lc);
    }

    public static MigLayout createMigLayoutVertical() {
        LC lc = new LC();
        lc.flowY().fill().gridGap("0!", "0!")
                .insets("0");

        return new MigLayout(lc);
    }

    public static ComboBox<String> addDropdownSelectBox(JPanel container, String name, ComboBox<String> comboBox) {
        container.add(new JLabel(name));
        container.add(comboBox, new CC().wrap());
        container.add(new JLabel(), new CC().wrap());
        return comboBox;
    }


    public static void addButton(JPanel container, JButton button, Consumer<ActionEvent> accept) {
        button.addActionListener(accept::accept);
        container.add(button);
    }

    /**
     *
     * @param name
     * @return
     */
    public static JLabel createEqualWidthLabel(String name) {
        JLabel label = new JLabel(name);
        label.setPreferredSize(new Dimension(100, 30));

        return label;
    }
}
