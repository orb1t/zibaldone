/*
 * Created 31-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.swing.JLabel;
import javax.swing.JPanel;
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

    private static final Logger log = Logger.getLogger(ImporterView.class.getName());

    private final Class<Importer> klass;

    private final Properties properties;

    private final ImporterViewForm gui;

    private final JungMainController controller;

    private List<JTextField> lockdown = Lists.newArrayList();

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

        if (properties.size() == 0) {
            gui.getjXReloadButton().setText("Import");
        }

        if (klass == null) {
            gui.getjXReloadButton().setVisible(false);
        }

        gui.getjXReloadButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                for (JTextField field : lockdown) {
                    field.setEditable(false);
                    field.setForeground(Color.gray);
                }
                if (gui.getjXReloadButton().getText().equals("Import")) {
                    gui.getjXReloadButton().setText("Reload");
                }

                controller.getImporterController().doImport(klass, properties);

                setSpecial(false);
            }
        });

        for (String propertyName : instance.getSpecialPropertyNames()) {
            addPropertyToPane(propertyName, true);
        }
        for (String propertyName : instance.getPropertyNames()) {
            addPropertyToPane(propertyName, false);
        }
    }

    // NOTE: this is tedious
    // http://stackoverflow.com/questions/10840078
    // http://stackoverflow.com/questions/1767008
    private void addPropertyToPane(final String name, boolean special) {
        JLabel label = new JLabel();
        label.setText(name + ":");
        gui.getjPropertiesPanel().add(label);

        String property = properties.getProperty(name);
        if (special && !Strings.isNullOrEmpty(property)) {
            gui.getjPropertiesPanel().add(new JLabel(property));
        } else {
            final JTextField text;
            if (name.equals("password")) {
                text = new JPasswordField();
            } else {
                text = new JTextField();
            }
            if (special) {
                lockdown.add(text);
            }
            if (!Strings.isNullOrEmpty(property)) {
                text.setText(property);
                if (special) {
                    text.setEditable(false);
                }
            }
            gui.getjPropertiesPanel().add(text);
            // TODO: filename file chooser

            text.addKeyListener(new KeyAdapter() {

                @Override
                public void keyTyped(KeyEvent e) {
                    String value = text.getText();
                    properties.setProperty(name, value);
                }
            });
        }
    }

    // bit of a hack to remove oneself from a container
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
