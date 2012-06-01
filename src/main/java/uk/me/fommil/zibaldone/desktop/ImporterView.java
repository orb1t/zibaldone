/*
 * Created 31-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.awt.Color;
import java.awt.event.*;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.swing.JTextField;
import org.jdesktop.swingx.JXTaskPane;
import uk.me.fommil.beans.JavabeansEditorForm;
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

        JavabeansEditorForm specialEditor = new JavabeansEditorForm();
        specialEditor.setBean(instance.getSpecialProperties());
        
        gui = new ImporterViewForm();
        gui.getjPropertiesPanel().add(specialEditor);
        
        add(gui);

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
                controller.getImporterController().doImport(klass, properties);
            }
        });
        
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
