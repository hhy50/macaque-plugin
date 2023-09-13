package six.eared.macaque.plugin.idea.ui;


import com.intellij.ui.EditorTextField;
import com.intellij.ui.JBColor;
import six.eared.macaque.common.util.StringUtil;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;

import static six.eared.macaque.plugin.idea.ui.UiUtil.*;

public class InputUi extends JPanel {

    private String name;

    private JLabel label;

    private EditorTextField  textField;

    private boolean required = false;

    public InputUi(String name) {
        this(name, "");
    }
    private JLabel errMsgLabel = null;
    public InputUi(String name, String value) {
        super(createMigLayout());
        this.name = name;
        this.textField = new EditorTextField(value);
        this.label = createEqualWidthLabel(name);
        this.add(this.label);
        this.add(this.textField, fillX());

        errMsgLabel = new JLabel("");
        errMsgLabel.setPreferredSize(new Dimension(100, 30));
        errMsgLabel.setForeground(Color.red);
        this.add(errMsgLabel);
    }

    public String getName() {
        return this.name;
    }

    public void setValue(String value) {
        this.textField.setText(value);
    }

    public String getValue() {
        return this.textField.getText();
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean checkRequired() {
        if (this.required) {
            if (StringUtil.isEmpty(getValue())) {
                errMsgLabel.setText("* required");
                return false;
            }else{
                errMsgLabel.setText("");
            }
        }
        return true;
    }

    public void reset() {
        this.textField.setBorder(null);
    }
}
