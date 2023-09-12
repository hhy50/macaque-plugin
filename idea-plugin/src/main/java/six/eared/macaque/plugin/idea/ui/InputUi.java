package six.eared.macaque.plugin.idea.ui;


import com.intellij.ui.EditorTextField;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import java.awt.*;

import static six.eared.macaque.plugin.idea.ui.UiUtil.*;

public class InputUi extends JPanel {

    private String name;

    private EditorTextField textField;

    public InputUi(String name) {
        this(name, "");
    }

    public InputUi(String name, String value) {
        super(createMigLayout());
        this.name = name;
        this.textField = new EditorTextField(value);
        this.add(createEqualWidthLabel(name));
        this.add(textField, fillX());
        backupBorder = this.textField.getBorder();
    }
    public void setTextFieldBorder(Color c){
        textField.setBorder(new LineBorder(c));
    }
    private Border backupBorder;
    public void resetTextFieldBorder(){
        textField.setBorder(backupBorder);
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
}
