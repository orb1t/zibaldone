/*
 * Created 01-Jun-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.beans.editors;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import javax.swing.*;
import org.jdesktop.swingx.JXTextField;

/**
 * Allows {@link PropertyEditor}s to be written as popups (by extending this)
 * with a text field (and icon) being provided here for convenience.
 * 
 * @author Samuel Halliday
 */
public abstract class JPropertyEditor extends PropertyEditorSupport {

    private final JXTextField textField = new JXTextField("→");

    {
        textField.setFocusable(false);
        textField.setEditable(false);
        textField.setBackground(null);
        textField.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                showEditor();
            }
        });
    }

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
        JPanel jp = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c1 = new GridBagConstraints();
        jp.setLayout(gridbag);

        c1.weightx = 1f;
        c1.anchor = GridBagConstraints.WEST;
        c1.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(textField, c1);
        jp.add(textField);

        Icon icon = getIcon();
        if (icon != null) {
            JButton button = new JButton(icon);
            button.addActionListener(action);
            button.setFocusable(false);
            GridBagConstraints c2 = new GridBagConstraints();
            c2.weightx = 0;
            c2.anchor = GridBagConstraints.EAST;
            c2.fill = GridBagConstraints.NONE;
            gridbag.setConstraints(button, c2);
            jp.add(button);
        }
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