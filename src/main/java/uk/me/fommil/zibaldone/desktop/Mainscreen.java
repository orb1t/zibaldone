/*
 * Created 24-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.collect.ListMultimap;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import java.beans.PropertyEditorManager;
import java.io.File;
import java.util.Date;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import org.jdesktop.swingx.combobox.MapComboBoxModel;
import uk.me.fommil.beans.editors.DatePropertyEditor;
import uk.me.fommil.beans.editors.FilePropertyEditor;
import uk.me.fommil.persistence.CrudDao;
import uk.me.fommil.zibaldone.Importer;
import uk.me.fommil.zibaldone.Importer.Settings;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Tag;
import uk.me.fommil.zibaldone.desktop.JungMainController.Relation;

/**
 * @author Samuel Halliday
 */
public class Mainscreen extends javax.swing.JFrame {

    private static final Logger log = Logger.getLogger(Mainscreen.class.getName());

    private static final long serialVersionUID = 1L;

    /** @param args */
    public static void main(String args[]) {
        PropertyEditorManager.registerEditor(File.class, FilePropertyEditor.class);
        PropertyEditorManager.registerEditor(Date.class, DatePropertyEditor.class);

        final EntityManagerFactory emf = CrudDao.createEntityManagerFactory("ZibaldonePU");

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                ObservableGraph<Note, Relation> graph = new ObservableGraph<Note, Relation>(new SparseMultigraph<Note, Relation>());

                Mainscreen main = new Mainscreen(emf, graph);
                main.setVisible(true);
            }
        });
    }

    /**
     * @return a suitable model for use in GUI Editors such as in Netbeans.
     */
    static ObservableGraph<Note, Relation> getGraphForTheBenefitOfNetbeans() {
        return new ObservableGraph<Note, Relation>(new SparseMultigraph<Note, Relation>());
    }

    private final JungMainController controller;

    private final ObservableGraph<Note, Relation> graph;

    /**
     * @deprecated only to be used by GUI Editors.
     */
    @Deprecated
    public Mainscreen() {
        this(null, getGraphForTheBenefitOfNetbeans());
    }

    /**
     * @param emf
     * @param graph
     */
    public Mainscreen(EntityManagerFactory emf, ObservableGraph<Note, Relation> graph) {
        this.graph = graph;
        rootPane.putClientProperty("apple.awt.brushMetalLook", Boolean.TRUE);
        controller = new JungMainController(emf, graph);

        initComponents();
        jSettingsTabs.setVisible(false);

        // TODO: dynamic lookup of available importers by querying controller
        MapComboBoxModel<String, Class<Importer>> importerChoices = new MapComboBoxModel<String, Class<Importer>>(controller.getImporterImplementations());
        jImporterSelectorComboBox.setModel(importerChoices);

        ListMultimap<Class<Importer>, Importer.Settings> importers = controller.getSettings().getImporters();
        for (Class<Importer> klass : importers.keySet()) {
            for (Settings settings : importers.get(klass)) {
                addImporter(klass, settings);
            }
        }
        // TODO: add the 'null' importer

        // TODO: animated settings panel
        // TODO: icons for the toolbar buttons
        // TODO: menu entries
        // TODO: use simplericity for a better OS X experience
    }

    private Importer.Settings addImporter(Class<Importer> klass, Importer.Settings settings) {
        Importer importer = ImporterController.forClass(klass, settings);
        ImporterController importerController = new ImporterController(controller, importer);
        ImporterView importerView = new ImporterView(importerController, settings != null);
        jXImportersPanel.add(importerView);
        jXImportersPanel.revalidate();
        return importerController.getImporter().getSettings();
    }

    /**
     * WARNING: Do NOT modify this code.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar = new javax.swing.JToolBar();
        jSearch = new org.jdesktop.swingx.JXSearchField();
        jCloudButton = new javax.swing.JToggleButton();
        jButtonClusters = new javax.swing.JToggleButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(50, 0), new java.awt.Dimension(1000, 0));
        jButtonSources = new javax.swing.JToggleButton();
        jJungPanel = new uk.me.fommil.zibaldone.desktop.JungGraphView(controller);
        jSettingsTabs = new javax.swing.JTabbedPane();
        jImportersPanel = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jAddImporterButton = new org.jdesktop.swingx.JXButton();
        jImporterSelectorComboBox = new javax.swing.JComboBox();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(500, 32767));
        jReloadImportersButton = new org.jdesktop.swingx.JXButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXImportersPanel = new org.jdesktop.swingx.JXTaskPaneContainer();
        jSynonymsPanel = new javax.swing.JPanel();
        jAdvancedPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Zibaldone");

        jToolBar.setFloatable(false);
        jToolBar.setRollover(true);
        jToolBar.setPreferredSize(new java.awt.Dimension(480, 42));

        jSearch.setMaximumSize(new java.awt.Dimension(1000, 2147483647));
        jSearch.setMinimumSize(new java.awt.Dimension(100, 28));
        jToolBar.add(jSearch);

        jCloudButton.setText("Tags");
        jToolBar.add(jCloudButton);

        jButtonClusters.setText("Clusters");
        jToolBar.add(jButtonClusters);
        jToolBar.add(filler1);

        jButtonSources.setText("Settings");
        jButtonSources.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jButtonSourcesStateChanged(evt);
            }
        });
        jToolBar.add(jButtonSources);

        getContentPane().add(jToolBar, java.awt.BorderLayout.PAGE_START);

        jJungPanel.setLayout(new java.awt.BorderLayout());
        getContentPane().add(jJungPanel, java.awt.BorderLayout.CENTER);

        jSettingsTabs.setPreferredSize(new java.awt.Dimension(320, 480));

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
        jToolBar1.add(jImporterSelectorComboBox);
        jToolBar1.add(filler2);

        jReloadImportersButton.setText("Reload All");
        jReloadImportersButton.setFocusable(false);
        jReloadImportersButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jReloadImportersButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jReloadImportersButton);

        jImportersPanel.add(jToolBar1, java.awt.BorderLayout.SOUTH);

        jScrollPane1.setBorder(null);
        jScrollPane1.setViewportView(null);
        jScrollPane1.setViewportView(jXImportersPanel);

        jImportersPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jSettingsTabs.addTab("Importers", jImportersPanel);

        jSynonymsPanel.setLayout(new java.awt.BorderLayout());
        jSettingsTabs.addTab("Synonyms", jSynonymsPanel);

        jAdvancedPanel.setLayout(new java.awt.BorderLayout());
        jSettingsTabs.addTab("Advanced", jAdvancedPanel);

        getContentPane().add(jSettingsTabs, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSourcesStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jButtonSourcesStateChanged
        jSettingsTabs.setVisible(jButtonSources.isSelected());
    }//GEN-LAST:event_jButtonSourcesStateChanged

    @SuppressWarnings("unchecked")
    private void jAddImporterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAddImporterButtonActionPerformed
        String name = (String) jImporterSelectorComboBox.getSelectedItem();
        Class<Importer> klass = controller.getImporterImplementations().get(name);
        Importer.Settings settings = addImporter(klass, null);
        controller.getSettings().getImporters().put(klass, settings);
    }//GEN-LAST:event_jAddImporterButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private org.jdesktop.swingx.JXButton jAddImporterButton;
    private javax.swing.JPanel jAdvancedPanel;
    private javax.swing.JToggleButton jButtonClusters;
    private javax.swing.JToggleButton jButtonSources;
    private javax.swing.JToggleButton jCloudButton;
    private javax.swing.JComboBox jImporterSelectorComboBox;
    private javax.swing.JPanel jImportersPanel;
    private uk.me.fommil.zibaldone.desktop.JungGraphView jJungPanel;
    private org.jdesktop.swingx.JXButton jReloadImportersButton;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXSearchField jSearch;
    private javax.swing.JTabbedPane jSettingsTabs;
    private javax.swing.JPanel jSynonymsPanel;
    private javax.swing.JToolBar jToolBar;
    private javax.swing.JToolBar jToolBar1;
    private org.jdesktop.swingx.JXTaskPaneContainer jXImportersPanel;
    // End of variables declaration//GEN-END:variables
}
