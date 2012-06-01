/*
 * Created 01-Jun-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.beans;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import javax.swing.*;

/**
 * Allows {@link PropertyEditor}s to be written as popups (by extending this)
 * with a text field and button being provided here for convenience.
 */
public abstract class AbstractSwingPropertyEditor extends PropertyEditorSupport {

    private final JTextField textField = new JTextField();

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }
    private final ActionListener action = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            showEditor();
        }
    };

    @Override
    public void setValue(Object value) {
        super.setValue(value);
        textField.setText(getAsText());
    }

    @Override
    public Component getCustomEditor() {
        Icon icon = getIcon();
        JButton button;
        if (icon == null) {
            button = new JButton("Edit");
        } else {
            button = new JButton(icon);
        }
        button.addActionListener(action);

        JPanel jp = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        jp.setLayout(gridbag);

        c.weightx = 1f;
        c.fill = GridBagConstraints.HORIZONTAL;
        textField.setEditable(false);
        gridbag.setConstraints(textField, c);
        jp.add(textField);

        c.weightx = 0;
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.NONE;
        gridbag.setConstraints(button, c);
        jp.add(button);
        return jp;
    }

    /**
     * Trigger for the custom editor to appear (in a pop-up or otherwise).
     * This should only exit when the custom editor has selected an item.
     * Remember to set the value by calling {@link #setValue(Object)}
     */
    public abstract void showEditor();

    /**
     * Intentionally for sub-classing.
     * 
     * @return icon as a prompt for the user to click for the popup. {@code null}
     * will be replaced by a text button.
     */
    public Icon getIcon() {
        return null;
    }
}