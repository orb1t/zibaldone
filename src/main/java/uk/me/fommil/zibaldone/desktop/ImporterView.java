/*
 * Created 31-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import javax.annotation.Nullable;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.jdesktop.swingx.JXTaskPane;
import uk.me.fommil.zibaldone.Importer;

/**
 * Displays a user-editable {@link Importer}.
 * 
 * @author Samuel Halliday
 */
public class ImporterView extends JXTaskPane {

    private final Class<Importer> klass;

    private final Properties properties;

    private final ImporterViewForm gui;

    private final JungMainController controller;

    /**
     * @param controller
     * @param klass {@code null} for the Notes with no importers.
     * @param properties
     */
    public ImporterView(final JungMainController controller, @Nullable final Class<Importer> klass, final Properties properties) throws InstantiationException, IllegalAccessException {
        super();
        Preconditions.checkNotNull(controller);
        Preconditions.checkNotNull(properties);
        this.controller = controller;
        this.klass = klass;
        this.properties = properties;

        final Importer instance = klass.newInstance();
        setTitle(instance.getName());

        gui = new ImporterViewForm();
        add(gui);

        if (klass == null) {
            gui.getjXReloadButton().setVisible(false);
//            gui.getjXRemoveButton().setVisible(false);
        }

        gui.getjXReloadButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                // TODO: check all special properties are completed
                // TODO: lock special property fields, if not already
                
                for (String name : instance.getSpecialPropertyNames()) {
//                    gui.getjPropertiesPanel().get
                    
                }
                
                controller.getImporterController().doImport(klass, properties);
            }
        });

        for (String propertyName : instance.getSpecialPropertyNames()) {
            addPropertyToPane(propertyName, true);
        }
        for (String propertyName : instance.getPropertyNames()) {
            addPropertyToPane(propertyName, false);
        }
    }

    private void addPropertyToPane(String name, boolean special) {
        
        // NOTE: this feel tedious - surely there is an easy way to create
        // a user input like this?
        
        JLabel label = new JLabel();
        label.setText(name + ":");
        gui.getjPropertiesPanel().add(label);

        String property = properties.getProperty(name);
        if (special && !Strings.isNullOrEmpty(property)) {
            gui.getjPropertiesPanel().add(new JLabel(property));
        } else if (name.equals("password")) {
            JPasswordField password = new JPasswordField();
            if (!Strings.isNullOrEmpty(property)) {
                password.setText(property);
            }
            gui.getjPropertiesPanel().add(password);
        } else {
            JTextField text = new JTextField();
            if (!Strings.isNullOrEmpty(property)) {
                text.setText(property);
            }
            gui.getjPropertiesPanel().add(text);
            // TODO: filename file chooser
        }
    }

    // bit of a hack to remove oneself from a container, but this is so
    public void addSelfRemovalListener(final ActionListener listener) {
        gui.getjXRemoveButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                controller.getImporterController().doRemove(klass, properties);

                listener.actionPerformed(new ActionEvent(this, 0, "self removal"));
            }
        });
    }
}
