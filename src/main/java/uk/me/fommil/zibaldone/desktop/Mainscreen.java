/*
 * Created 24-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.base.Preconditions;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import java.beans.PropertyEditorManager;
import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import javax.persistence.EntityManagerFactory;
import javax.swing.ComboBoxModel;
import lombok.extern.java.Log;
import org.jdesktop.swingx.combobox.MapComboBoxModel;
import uk.me.fommil.beans.editors.DatePropertyEditor;
import uk.me.fommil.beans.editors.FilePropertyEditor;
import uk.me.fommil.persistence.CrudDao;
import uk.me.fommil.zibaldone.Importer;
import uk.me.fommil.zibaldone.Note;

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
                ObservableGraph<Note, Weight> graph = new ObservableGraph<Note, Weight>(new UndirectedSparseGraph<Note, Weight>());
//                ObservableGraph<Note, Weight> graph = getGraphForTheBenefitOfNetbeans();

                JungMainController controller = new JungMainController(emf, graph);
                Mainscreen main = new Mainscreen();
                main.setController(controller);
                main.setVisible(true);
            }
        });
    }

    private JungMainController controller;

    public Mainscreen() {
        rootPane.putClientProperty("apple.awt.brushMetalLook", Boolean.TRUE);
        initComponents();
        settingsPanel.setVisible(false);
    }
    
    /**
     * @param controller
     */
    public void setController(JungMainController controller) {
        Preconditions.checkNotNull(controller);
        this.controller = controller;

        jungGraphView.setGraph(controller.getGraph());
        tagSelectView.setController(controller);
        
        controller.addClustersChangedListener(jungGraphView);
        controller.addTagsChangedListener(tagSelectView);

        // TODO: dynamic lookup of available importers by querying controller
        Map<String, Class<Importer>> importers = ImporterController.getImporterImplementations();
        ComboBoxModel importerChoices = new MapComboBoxModel<String, Class<Importer>>(importers);
        importerSelector.setModel(importerChoices);
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
        importersPanel.add(importerView);
        importersPanel.revalidate();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tagDialog = new javax.swing.JDialog();
        tagSelectView = new uk.me.fommil.zibaldone.desktop.TagSelectView();
        javax.swing.JToolBar jToolBar = new javax.swing.JToolBar();
        org.jdesktop.swingx.JXSearchField jSearch = new org.jdesktop.swingx.JXSearchField();
        javax.swing.JToggleButton tagsButton = new javax.swing.JToggleButton();
        javax.swing.JToggleButton jButtonClusters = new javax.swing.JToggleButton();
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

        tagDialog.setMinimumSize(new java.awt.Dimension(300, 300));
        tagDialog.setModal(true);
        tagDialog.getContentPane().add(tagSelectView, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Zibaldone");
        setMinimumSize(new java.awt.Dimension(900, 600));

        jToolBar.setFloatable(false);
        jToolBar.setRollover(true);
        jToolBar.setPreferredSize(new java.awt.Dimension(480, 42));

        jSearch.setMaximumSize(new java.awt.Dimension(1000, 2147483647));
        jSearch.setMinimumSize(new java.awt.Dimension(100, 28));
        jToolBar.add(jSearch);

        tagsButton.setText("Tags");
        tagsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tagsButtonActionPerformed(evt);
            }
        });
        jToolBar.add(tagsButton);

        jButtonClusters.setText("Clusters");
        jToolBar.add(jButtonClusters);

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
        controller.getSettings().getImporters().put(pair.getKey(), pair.getValue());
        addImporter(pair.getKey(), false);
    }//GEN-LAST:event_jAddImporterButtonActionPerformed

    private void tagsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tagsButtonActionPerformed
        tagDialog.setVisible(true);
    }//GEN-LAST:event_tagsButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JComboBox importerSelector;
    org.jdesktop.swingx.JXTaskPaneContainer importersPanel;
    uk.me.fommil.zibaldone.desktop.JungGraphView jungGraphView;
    javax.swing.JToggleButton settingsButton;
    javax.swing.JTabbedPane settingsPanel;
    javax.swing.JDialog tagDialog;
    uk.me.fommil.zibaldone.desktop.TagSelectView tagSelectView;
    // End of variables declaration//GEN-END:variables
}
