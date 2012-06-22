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
import org.jdesktop.swingx.combobox.MapComboBoxModel;
import uk.me.fommil.beans.editors.DatePropertyEditor;
import uk.me.fommil.beans.editors.FilePropertyEditor;
import uk.me.fommil.zibaldone.Importer;
import uk.me.fommil.zibaldone.Importer.Settings;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Tag;

/**
 * @author Samuel Halliday
 */
public class Mainscreen extends javax.swing.JFrame {

    private static final Logger log = Logger.getLogger(Mainscreen.class.getName());

    /** @param args */
    public static void main(String args[]) {
        PropertyEditorManager.registerEditor(File.class, FilePropertyEditor.class);
        PropertyEditorManager.registerEditor(Date.class, DatePropertyEditor.class);

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                ObservableGraph<Note, Double> graph = new ObservableGraph<Note, Double>(new SparseMultigraph<Note, Double>());

                Mainscreen main = new Mainscreen(graph);
                main.setVisible(true);
            }
        });
    }

    /**
     * @return a suitable model for use in GUI Editors such as in Netbeans.
     */
    static ObservableGraph<Note, Double> getGraphForTheBenefitOfNetbeans() {
        ObservableGraph<Note, Double> graph = new ObservableGraph<Note, Double>(new SparseMultigraph<Note, Double>());

        Note a = new Note();
        a.setTitle("A");
        a.setTags(Tag.asTags("smug", "silly"));
        Note b = new Note();
        b.setTitle("B");
        b.setTags(Tag.asTags("smug", "lovely"));
        Note c = new Note();
        c.setTitle("C");
        c.setTags(Tag.asTags("smug", "lovely"));

        graph.addVertex(a);
        graph.addVertex(b);
        graph.addVertex(c);

        graph.addEdge(0.1, a, b);
        graph.addEdge(0.4, a, c);

        return graph;
    }

    private final JungMainController controller;

    private final ObservableGraph<Note, Double> graph;

    /**
     * @deprecated only to be used by GUI Editors.
     */
    @Deprecated
    public Mainscreen() {
        this(getGraphForTheBenefitOfNetbeans());
    }

    public Mainscreen(ObservableGraph<Note, Double> graph) {
        rootPane.putClientProperty("apple.awt.brushMetalLook", Boolean.TRUE);
        this.graph = graph;
        controller = new JungMainController(graph);


        initComponents();
        jSettingsPanel.setVisible(false);

        // TODO: dynamic lookup of available importers by querying controller
        MapComboBoxModel<String, Class<Importer>> importerChoices = new MapComboBoxModel<String, Class<Importer>>(controller.getImporterImplementations());
        jImporterSelectorComboBox.setModel(importerChoices);

        ListMultimap<Class<Importer>, Importer.Settings> importers = controller.getSettings().getImporters();
        for (Class<Importer> klass : importers.keySet()) {
            for (Settings settings : importers.get(klass)) {
                ImporterController importerController = ImporterController.forClass(klass, settings);
                ImporterView importerView = new ImporterView(importerController, true);
                jXImportersPanel.add(importerView);
            }
        }

        // TODO: add the 'null' importer

        // TODO: animated settings panel
        // TODO: icons for the toolbar buttons
        // TODO: menu entries
        // TODO: use simplericity for a better OS X experience
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
        jJungPanel = new uk.me.fommil.zibaldone.desktop.JungGraphView(graph, controller);
        jSettingsPanel = new javax.swing.JPanel();
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

        jSettingsPanel.setPreferredSize(new java.awt.Dimension(320, 400));
        jSettingsPanel.setLayout(new java.awt.BorderLayout());

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

        jSettingsPanel.add(jSettingsTabs, java.awt.BorderLayout.CENTER);

        getContentPane().add(jSettingsPanel, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSourcesStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jButtonSourcesStateChanged
        jSettingsPanel.setVisible(jButtonSources.isSelected());
    }//GEN-LAST:event_jButtonSourcesStateChanged

    @SuppressWarnings("unchecked")
    private void jAddImporterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAddImporterButtonActionPerformed
        String name = (String) jImporterSelectorComboBox.getSelectedItem();
        Class<Importer> klass = controller.getImporterImplementations().get(name);

        ImporterController importerController = ImporterController.forClass(klass, null);
        ImporterView importerView = new ImporterView(importerController, false);
        jXImportersPanel.add(importerView);

        Importer.Settings settings = importerController.getSettings();
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
    private javax.swing.JPanel jSettingsPanel;
    private javax.swing.JTabbedPane jSettingsTabs;
    private javax.swing.JPanel jSynonymsPanel;
    private javax.swing.JToolBar jToolBar;
    private javax.swing.JToolBar jToolBar1;
    private org.jdesktop.swingx.JXTaskPaneContainer jXImportersPanel;
    // End of variables declaration//GEN-END:variables
}
