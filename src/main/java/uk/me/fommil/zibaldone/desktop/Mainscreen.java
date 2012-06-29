/*
 * Created 24-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import java.beans.PropertyEditorManager;
import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import javax.persistence.EntityManagerFactory;
import lombok.extern.java.Log;
import org.jdesktop.swingx.combobox.MapComboBoxModel;
import uk.me.fommil.beans.editors.DatePropertyEditor;
import uk.me.fommil.beans.editors.FilePropertyEditor;
import uk.me.fommil.persistence.CrudDao;
import uk.me.fommil.zibaldone.Importer;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Tag;

/**
 * @author Samuel Halliday
 */
@Log
public class Mainscreen extends javax.swing.JFrame {

    /** @param args */
    public static void main(String args[]) {
        PropertyEditorManager.registerEditor(File.class, FilePropertyEditor.class);
        PropertyEditorManager.registerEditor(Date.class, DatePropertyEditor.class);

        final EntityManagerFactory emf = CrudDao.createEntityManagerFactory("ZibaldonePU");

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                ObservableGraph<Note, Double> graph = new ObservableGraph<Note, Double>(new UndirectedSparseGraph<Note, Double>());
//                ObservableGraph<Note, Double> graph = getGraphForTheBenefitOfNetbeans();

                Mainscreen main = new Mainscreen(emf, graph);
                main.setVisible(true);
            }
        });
    }

    /**
     * @return a suitable model for use in GUI Editors such as in Netbeans.
     */
    static ObservableGraph<Note, Double> getGraphForTheBenefitOfNetbeans() {        
        ObservableGraph<Note, Double> graph = new ObservableGraph<Note, Double>(new UndirectedSparseGraph<Note, Double>());
        Note a = new Note();
        a.setTags(Tag.asTags("smug", "silly", "ugly"));
        Note b = new Note();
        b.setTags(Tag.asTags("smug", "silly"));
        Note c = new Note();
        c.setTags(Tag.asTags("ugly"));
        graph.addVertex(a);
        graph.addVertex(b);
        graph.addVertex(c);
        graph.addEdge(0.33, a, b);
        graph.addEdge(0.67, a, c);
        return graph;
    }

    private final JungMainController controller;

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
    public Mainscreen(EntityManagerFactory emf, ObservableGraph<Note, Double> graph) {
        rootPane.putClientProperty("apple.awt.brushMetalLook", Boolean.TRUE);
        controller = new JungMainController(emf, graph);

        initComponents();
        jSettingsTabs.setVisible(false);

        // TODO: dynamic lookup of available importers by querying controller
        Map<String, Class<Importer>> importers = ImporterController.getImporterImplementations();
        MapComboBoxModel<String, Class<Importer>> importerChoices = new MapComboBoxModel<String, Class<Importer>>(importers);
        jImporterSelectorComboBox.setModel(importerChoices);

        for (UUID uuid : controller.getSettings().getImporters().keySet()) {
            addImporter(uuid, true);
        }

        // TODO: add the 'null' importer        
        // TODO: JSplitPane for settings size
        // TODO: animated settings panel
        // TODO: icons for the toolbar buttons
        // TODO: menu entries
        // TODO: use simplericity for a better OS X experience
    }

    private void addImporter(UUID uuid, boolean used) {
        ImporterController importerController = new ImporterController(controller, uuid);
        ImporterView importerView = new ImporterView(importerController, used);
        jXImportersPanel.add(importerView);
        jXImportersPanel.revalidate();
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
        jButtonLayout = new javax.swing.JToggleButton();
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
        jAdvancedPanel = new javax.swing.JPanel();
        jSynonymsPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Zibaldone");
        setMinimumSize(new java.awt.Dimension(900, 600));

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

        jButtonLayout.setText("Relators");
        jButtonLayout.setFocusable(false);
        jButtonLayout.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonLayout.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar.add(jButtonLayout);
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

        jAdvancedPanel.setLayout(new java.awt.BorderLayout());
        jSettingsTabs.addTab("Relators", jAdvancedPanel);

        jSynonymsPanel.setLayout(new java.awt.BorderLayout());
        jSettingsTabs.addTab("Synonyms", jSynonymsPanel);

        getContentPane().add(jSettingsTabs, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSourcesStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jButtonSourcesStateChanged
        jSettingsTabs.setVisible(jButtonSources.isSelected());
    }//GEN-LAST:event_jButtonSourcesStateChanged

    @SuppressWarnings("unchecked")
    private void jAddImporterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAddImporterButtonActionPerformed
        String name = (String) jImporterSelectorComboBox.getSelectedItem();
        Entry<UUID, Importer> pair = ImporterController.newImporter(name);
        controller.getSettings().getImporters().put(pair.getKey(), pair.getValue());
        addImporter(pair.getKey(), false);
    }//GEN-LAST:event_jAddImporterButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private org.jdesktop.swingx.JXButton jAddImporterButton;
    private javax.swing.JPanel jAdvancedPanel;
    private javax.swing.JToggleButton jButtonClusters;
    private javax.swing.JToggleButton jButtonLayout;
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
