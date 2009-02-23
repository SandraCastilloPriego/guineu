

package guineu.util.components;

import java.awt.Font;

import javax.swing.JCheckBox;

/**
 * Checkbox wrapper class
 */
public class ExtendedCheckBox<Type> extends JCheckBox {

    static final Font checkBoxFont = new Font("SansSerif", Font.PLAIN, 11);

    private Type object;

    public ExtendedCheckBox(Type object) {
        this(object, false);
    }
    
    public ExtendedCheckBox(Type object, boolean selected) {
        super(object.toString(), selected);
        this.object = object;
        setOpaque(false);
        setFont(checkBoxFont);
    }

    /**
     * @return Returns the dataFile.
     */
    public Type getObject() {
        return object;
    }

    public int getPreferredWidth() {
        return ((int) getPreferredSize().getWidth()) + 30;
    }

}
