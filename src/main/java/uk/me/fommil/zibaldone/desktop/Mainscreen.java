/*
 * Created 24-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import uk.me.fommil.zibaldone.control.GraphController;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditorManager;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;
import javax.persistence.EntityManagerFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JFrame;
import lombok.BoundSetter;
import lombok.extern.java.Log;
import org.jdesktop.swingx.combobox.MapComboBoxModel;
import uk.me.fommil.beans.editors.DatePropertyEditor;
import uk.me.fommil.beans.editors.FilePropertyEditor;
import uk.me.fommil.persistence.CrudDao;
import uk.me.fommil.swing.SwingConvenience;
import uk.me.fommil.zibaldone.Importer;
import uk.me.fommil.zibaldone.control.BunchController;
import uk.me.fommil.zibaldone.control.ImporterController;
import uk.me.fommil.zibaldone.control.Settings;
import uk.me.fommil.zibaldone.control.TagController;
import uk.me.fommil.zibaldone.importer.OrgModeImporter;

/**
 * @author Samuel Halliday
 */
@Log
public final class Mainscreen extends JFrame implements PropertyChangeListener {

    public static void main(String args[]) throws Exception {
        PropertyEditorManager.registerEditor(File.class, FilePropertyEditor.class);
        PropertyEditorManager.registerEditor(Date.class, DatePropertyEditor.class);

        EntityManagerFactory emf = CrudDao.createEntityManagerFactory("ZibaldonePU");

        final Settings settings;
        {
            final File settingsFile = new File("zibaldone.xml");
            if (settingsFile.exists()) {
                FileInputStream in = Files.newInputStreamSupplier(settingsFile).getInput();
                XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(in));
                settings = (Settings) decoder.readObject();
                decoder.close();
            } else {
                settings = new Settings();
            }

            settings.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    try {
                        log.info("Saving Settings: " + evt.getPropertyName());
                        FileOutputStream out = Files.newOutputStreamSupplier(settingsFile).getOutput();
                        XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(out));
                        encoder.writeObject(settings);
                        encoder.close();
                    } catch (IOException e) {
                        log.log(Level.WARNING, "Problem saving " + settingsFile.getName(), e);
                    }
                }
            });
        }

        GraphController graphController = new GraphController(emf, settings);
        TagController tagController = new TagController(settings);
        BunchController bunchController = new BunchController(emf, settings);
        ImporterController importerController = new ImporterController(emf, settings);
        importerController.addTagListener(graphController);
        importerController.addNoteListener(graphController);
        tagController.addTagListener(graphController);

        // TODO: Swing Log capture and view
        Mainscreen main = new Mainscreen();
        main.setTagController(tagController);
        main.setGraphController(graphController);
        main.setBunchController(bunchController);
        main.setImporterController(importerController);
        main.setSettings(settings);
        main.setVisible(true);

        {
            // DEBUG: programmatic load of an importer
            UUID uuid = UUID.randomUUID();
            Map<UUID, Importer> importers = settings.getImporters();
            OrgModeImporter importer = new OrgModeImporter();
            importer.getSettings().setFile(new File("/Users/samuel/QT2-notes.org"));
            importers.put(uuid, importer);
            try {
                importerController.doImport(uuid);
            } catch (IOException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
    }

    @BoundSetter
    private GraphController graphController;

    @BoundSetter
    private TagController tagController;

    @BoundSetter
    private BunchController bunchController;

    @BoundSetter
    private ImporterController importerController;

    @BoundSetter
    private Settings settings;

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Preconditions.checkNotNull(evt);
        String property = evt.getPropertyName();
        log.fine("Changed " + property);
        // TODO: remove older listeners
        if ("graphController".equals(property)) {
            graphController.addClusterListener(jungGraphView);
            jungGraphView.setGraph(graphController.getGraph());
        } else if ("tagController".equals(property)) {
            tagController.addTagListener(tagSelectView);
            tagSelectView.setTagController(tagController);
        } else if ("bunchController".equals(property)) {
            bunchController.addBunchListener(jungGraphView);
            bunchController.addBunchListener(bunchMenu);
            jungGraphView.setBunchController(bunchController);
            bunchMenu.setBunchController(bunchController);
        } else if ("importerController".equals(property)) {
            importerController.addTagListener(tagSelectView);
        } else if ("settings".equals(property)) {
            for (UUID uuid : settings.getImporters().keySet()) {
                addImporter(uuid, true);
            }
        }
    }

    public Mainscreen() {
        super();
        rootPane.putClientProperty("apple.awt.brushMetalLook", Boolean.TRUE);
        initComponents();
        settingsPanel.setVisible(false);

        // FIXME: dynamic lookup of available importers by querying 'settings'
        Map<String, Class<Importer>> importers = ImporterController.getImporterImplementations();
        ComboBoxModel importerChoices = new MapComboBoxModel<String, Class<Importer>>(importers);
        importerSelector.setModel(importerChoices);

        addPropertyChangeListener(this);



        // TODO: add the 'null' importer        
        // TODO: animated settings panel
        // TODO: icons for the toolbar buttons
        // TODO: menu entries
        // TODO: use simplericity for a better OS X experience
    }

    private void addImporter(UUID uuid, boolean used) {
        ImporterView importerView = new ImporterView();
        importerView.setImporterController(importerController);
        importerView.setUuid(uuid);
        importerView.setCollapsed(used);
        importersPanel.add(importerView);
        importersPanel.revalidate();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tagDialog = new javax.swing.JDialog();
        tagSelectView = new uk.me.fommil.zibaldone.desktop.TagsView();
        bunchMenu = new uk.me.fommil.zibaldone.desktop.BunchMenu();
        javax.swing.JToolBar jToolBar = new javax.swing.JToolBar();
        org.jdesktop.swingx.JXSearchField jSearch = new org.jdesktop.swingx.JXSearchField();
        javax.swing.JButton tagsButton = new javax.swing.JButton();
        bunchesButton = new javax.swing.JButton();
        javax.swing.JToggleButton jButtonLayout = new javax.swing.JToggleButton();
        javax.swing.Box.Filler filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(50, 0), new java.awt.Dimension(1000, 0));
        settingsButton = new javax.swing.JToggleButton();
        jungGraphView = new uk.me.fommil.zibaldone.desktop.JungGraphView();
        settingsPanel = new javax.swing.JTabbedPane();
        javax.swing.JPanel jImportersPanel = new javax.swing.JPanel();
        javax.swing.JToolBar jToolBar1 = new javax.swing.JToolBar();
        org.jdesktop.swingx.JXButton jAddImporterButton = new org.jdesktop.swingx.JXButton();
        importerSelector = new javax.swing.JComboBox();
        javax.swing.Box.Filler filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(500, 32767));
        org.jdesktop.swingx.JXButton jReloadImportersButton = new org.jdesktop.swingx.JXButton();
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        importersPanel = new org.jdesktop.swingx.JXTaskPaneContainer();
        javax.swing.JPanel jAdvancedPanel = new javax.swing.JPanel();
        javax.swing.JPanel jSynonymsPanel = new javax.swing.JPanel();

        tagDialog.setTitle("Tags");
        tagDialog.setAlwaysOnTop(true);
        tagDialog.setMinimumSize(new java.awt.Dimension(300, 300));
        tagDialog.setResizable(false);

        tagSelectView.setSelectable(true);
        tagDialog.getContentPane().add(tagSelectView, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Zibaldone");
        setMinimumSize(new java.awt.Dimension(900, 600));
        setSize(new java.awt.Dimension(800, 600));

        jToolBar.setFloatable(false);
        jToolBar.setRollover(true);
        jToolBar.setPreferredSize(new java.awt.Dimension(480, 42));

        jSearch.setMaximumSize(new java.awt.Dimension(1000, 2147483647));
        jSearch.setMinimumSize(new java.awt.Dimension(100, 28));
        jSearch.setPrompt("Search titles, tags and contents");
        jToolBar.add(jSearch);

        tagsButton.setText("Tags");
        tagsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tagsButtonActionPerformed(evt);
            }
        });
        jToolBar.add(tagsButton);

        bunchesButton.setText("Bunches");
        bunchesButton.setFocusable(false);
        bunchesButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bunchesButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bunchesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bunchesButtonActionPerformed(evt);
            }
        });
        jToolBar.add(bunchesButton);

        jButtonLayout.setText("Relators");
        jButtonLayout.setFocusable(false);
        jButtonLayout.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonLayout.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar.add(jButtonLayout);
        jToolBar.add(filler1);

        settingsButton.setText("Settings");
        settingsButton.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                settingsButtonStateChanged(evt);
            }
        });
        jToolBar.add(settingsButton);

        getContentPane().add(jToolBar, java.awt.BorderLayout.PAGE_START);
        getContentPane().add(jungGraphView, java.awt.BorderLayout.CENTER);

        settingsPanel.setPreferredSize(new java.awt.Dimension(320, 480));

        jImportersPanel.setLayout(new java.awt.BorderLayout());

        jToolBar1.setFloatable(false);

        jAddImporterButton.setText("+");
        jAddImporterButton.setFocusable(false);
        jAddImporterButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jAddImporterButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jAddImporterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAddImporterButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(jAddImporterButton);
        jToolBar1.add(importerSelector);
        jToolBar1.add(filler2);

        jReloadImportersButton.setText("Reload All");
        jReloadImportersButton.setFocusable(false);
        jReloadImportersButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jReloadImportersButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jReloadImportersButton);

        jImportersPanel.add(jToolBar1, java.awt.BorderLayout.SOUTH);

        jScrollPane1.setBorder(null);
        jScrollPane1.setViewportView(null);

        importersPanel.setLayout(new java.awt.FlowLayout());
        jScrollPane1.setViewportView(importersPanel);

        jImportersPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        settingsPanel.addTab("Importers", jImportersPanel);

        jAdvancedPanel.setLayout(new java.awt.BorderLayout());
        settingsPanel.addTab("Relators", jAdvancedPanel);

        jSynonymsPanel.setLayout(new java.awt.BorderLayout());
        settingsPanel.addTab("Synonyms", jSynonymsPanel);

        getContentPane().add(settingsPanel, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void settingsButtonStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_settingsButtonStateChanged
        settingsPanel.setVisible(settingsButton.isSelected());
    }//GEN-LAST:event_settingsButtonStateChanged

    @SuppressWarnings("unchecked")
    private void jAddImporterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAddImporterButtonActionPerformed
        String name = (String) importerSelector.getSelectedItem();
        Entry<UUID, Importer> pair = ImporterController.newImporter(name);
        settings.getImporters().put(pair.getKey(), pair.getValue());
        addImporter(pair.getKey(), false);
    }//GEN-LAST:event_jAddImporterButtonActionPerformed

    private void tagsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tagsButtonActionPerformed
        tagDialog.setVisible(true);
    }//GEN-LAST:event_tagsButtonActionPerformed

    private void bunchesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bunchesButtonActionPerformed
        SwingConvenience.popupAtMouse(bunchMenu, this);
    }//GEN-LAST:event_bunchesButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private uk.me.fommil.zibaldone.desktop.BunchMenu bunchMenu;
    private javax.swing.JButton bunchesButton;
    private javax.swing.JComboBox importerSelector;
    private org.jdesktop.swingx.JXTaskPaneContainer importersPanel;
    private uk.me.fommil.zibaldone.desktop.JungGraphView jungGraphView;
    private javax.swing.JToggleButton settingsButton;
    private javax.swing.JTabbedPane settingsPanel;
    private javax.swing.JDialog tagDialog;
    private uk.me.fommil.zibaldone.desktop.TagsView tagSelectView;
    // End of variables declaration//GEN-END:variables
}
