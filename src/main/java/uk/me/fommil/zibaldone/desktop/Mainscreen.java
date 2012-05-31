/*
 * Created 24-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Tag;

/**
 * @author Samuel Halliday
 */
public class Mainscreen extends javax.swing.JFrame {

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

    /**
     * @deprecated only to be used by GUI Editors.
     */
    @Deprecated
    public Mainscreen() {
        this(getGraphForTheBenefitOfNetbeans());
    }

    public Mainscreen(ObservableGraph<Note, Double> graph) {
        rootPane.putClientProperty("apple.awt.brushMetalLook", Boolean.TRUE);

        initComponents();

        jSettingsPanel.setVisible(false);
        controller = new JungMainController(graph);

        // TODO: animated settings panel
        // TODO: icons for the toolbar buttons
        // TODO: menu entries
        // TODO: use simplericity for a better OS X experience
    }

    /**
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFauxPanel = new javax.swing.JPanel();
        jToolBar = new javax.swing.JToolBar();
        jSearch = new org.jdesktop.swingx.JXSearchField();
        jCloudButton = new javax.swing.JToggleButton();
        jButtonClusters = new javax.swing.JToggleButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButtonSources = new javax.swing.JToggleButton();
        jSettingsPanel = new javax.swing.JPanel();
        jSettingsTabs = new javax.swing.JTabbedPane();
        jImportersPanel = new javax.swing.JPanel();
        jSynonymsPanel = new javax.swing.JPanel();
        jAdvancedPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jFauxPanel.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout jFauxPanelLayout = new javax.swing.GroupLayout(jFauxPanel);
        jFauxPanel.setLayout(jFauxPanelLayout);
        jFauxPanelLayout.setHorizontalGroup(
            jFauxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 670, Short.MAX_VALUE)
        );
        jFauxPanelLayout.setVerticalGroup(
            jFauxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 463, Short.MAX_VALUE)
        );

        jToolBar.setFloatable(false);
        jToolBar.setRollover(true);
        jToolBar.setBorderPainted(false);
        jToolBar.add(jSearch);

        jCloudButton.setText("Tags");
        jToolBar.add(jCloudButton);

        jButtonClusters.setText("Clusters");
        jToolBar.add(jButtonClusters);

        jSeparator1.setPreferredSize(new java.awt.Dimension(90, 1));
        jToolBar.add(jSeparator1);

        jButtonSources.setText("Settings");
        jButtonSources.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jButtonSourcesStateChanged(evt);
            }
        });
        jToolBar.add(jButtonSources);

        javax.swing.GroupLayout jImportersPanelLayout = new javax.swing.GroupLayout(jImportersPanel);
        jImportersPanel.setLayout(jImportersPanelLayout);
        jImportersPanelLayout.setHorizontalGroup(
            jImportersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 305, Short.MAX_VALUE)
        );
        jImportersPanelLayout.setVerticalGroup(
            jImportersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 417, Short.MAX_VALUE)
        );

        jSettingsTabs.addTab("Importers", jImportersPanel);

        javax.swing.GroupLayout jSynonymsPanelLayout = new javax.swing.GroupLayout(jSynonymsPanel);
        jSynonymsPanel.setLayout(jSynonymsPanelLayout);
        jSynonymsPanelLayout.setHorizontalGroup(
            jSynonymsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 305, Short.MAX_VALUE)
        );
        jSynonymsPanelLayout.setVerticalGroup(
            jSynonymsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 417, Short.MAX_VALUE)
        );

        jSettingsTabs.addTab("Synonyms", jSynonymsPanel);

        javax.swing.GroupLayout jAdvancedPanelLayout = new javax.swing.GroupLayout(jAdvancedPanel);
        jAdvancedPanel.setLayout(jAdvancedPanelLayout);
        jAdvancedPanelLayout.setHorizontalGroup(
            jAdvancedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 305, Short.MAX_VALUE)
        );
        jAdvancedPanelLayout.setVerticalGroup(
            jAdvancedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 417, Short.MAX_VALUE)
        );

        jSettingsTabs.addTab("Advanced", jAdvancedPanel);

        javax.swing.GroupLayout jSettingsPanelLayout = new javax.swing.GroupLayout(jSettingsPanel);
        jSettingsPanel.setLayout(jSettingsPanelLayout);
        jSettingsPanelLayout.setHorizontalGroup(
            jSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSettingsTabs, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jSettingsPanelLayout.setVerticalGroup(
            jSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSettingsTabs)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, 670, Short.MAX_VALUE)
            .addComponent(jFauxPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(0, 344, Short.MAX_VALUE)
                    .addComponent(jSettingsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jFauxPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(0, 44, Short.MAX_VALUE)
                    .addComponent(jSettingsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSourcesStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jButtonSourcesStateChanged
        jSettingsPanel.setVisible(jButtonSources.isSelected());
    }//GEN-LAST:event_jButtonSourcesStateChanged

    /** @param args */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                ObservableGraph<Note, Double> graph = new ObservableGraph<Note, Double>(new SparseMultigraph<Note, Double>());

                Mainscreen main = new Mainscreen(graph);
                main.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jAdvancedPanel;
    private javax.swing.JToggleButton jButtonClusters;
    private javax.swing.JToggleButton jButtonSources;
    private javax.swing.JToggleButton jCloudButton;
    private javax.swing.JPanel jFauxPanel;
    private javax.swing.JPanel jImportersPanel;
    private org.jdesktop.swingx.JXSearchField jSearch;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JPanel jSettingsPanel;
    private javax.swing.JTabbedPane jSettingsTabs;
    private javax.swing.JPanel jSynonymsPanel;
    private javax.swing.JToolBar jToolBar;
    // End of variables declaration//GEN-END:variables
}
