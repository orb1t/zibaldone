/*
 * Created 24-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import uk.me.fommil.zibaldone.Importer;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Tag;

/**
 * @author Samuel Halliday
 */
public class Mainscreen extends javax.swing.JFrame {

    private static final Logger log = Logger.getLogger(Mainscreen.class.getName());

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

    private final Map<String, String> importerImpls = Maps.newHashMap();

    /**
     * @deprecated only to be used by GUI Editors.
     */
    @Deprecated
    public Mainscreen() {
        this(getGraphForTheBenefitOfNetbeans());
    }

    public Mainscreen(ObservableGraph<Note, Double> graph) {
        this.graph = graph;
        controller = new JungMainController(graph);

        ServiceLoader<Importer> importerService = ServiceLoader.load(Importer.class);
        for (Importer importer : importerService) {
            String name = importer.getName();
            String klass = importer.getClass().getCanonicalName();
            importerImpls.put(name, klass);
        }
        
        rootPane.putClientProperty("apple.awt.brushMetalLook", Boolean.TRUE);
        initComponents();
        jSettingsPanel.setVisible(false);
        
        DefaultComboBoxModel importerChoices = (DefaultComboBoxModel) jImporterSelectorComboBox.getModel();
        importerChoices.removeAllElements();
        for (String name : importerImpls.keySet()) {
            importerChoices.addElement(name);
        }
        
        ListMultimap<String, Properties> importers = controller.getSettings().getImporters();
        for (String klassName : importers.keySet()) {
            for (Properties properties : importers.get(klassName)) {
                addImporterView(klassName, properties);
            }
        }
        
        // TODO: add the 'null' importer

        // TODO: animated settings panel
        // TODO: icons for the toolbar buttons
        // TODO: menu entries
        // TODO: use simplericity for a better OS X experience
    }

    private void addImporterView(String klassName, Properties properties) {
        Class<Importer> klass;
        final ImporterView importerView;
        try {
            klass = (Class<Importer>) Class.forName(klassName);
            importerView = new ImporterView(controller, klass, properties);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        jXImportersContainer.add(importerView);
        importerView.addSelfRemovalListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                jXImportersContainer.remove(importerView);
                jXImportersContainer.revalidate();
                validate();
            }
        });

        jXImportersContainer.revalidate();
        validate();
    }

    /**
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jJungPanel = new uk.me.fommil.zibaldone.desktop.JungGraphView(graph, controller);
        jSettingsPanel = new javax.swing.JPanel();
        jSettingsTabs = new javax.swing.JTabbedPane();
        jImportersPanel = new javax.swing.JPanel();
        jAddImporterButton = new org.jdesktop.swingx.JXButton();
        jReloadImportersButton = new org.jdesktop.swingx.JXButton();
        jImporterSelectorComboBox = new javax.swing.JComboBox();
        jImporterScrollPane = new javax.swing.JScrollPane();
        jXImportersContainer = new org.jdesktop.swingx.JXTaskPaneContainer();
        jSynonymsPanel = new javax.swing.JPanel();
        jAdvancedPanel = new javax.swing.JPanel();
        jToolBar = new javax.swing.JToolBar();
        jSearch = new org.jdesktop.swingx.JXSearchField();
        jCloudButton = new javax.swing.JToggleButton();
        jButtonClusters = new javax.swing.JToggleButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButtonSources = new javax.swing.JToggleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout jJungPanelLayout = new javax.swing.GroupLayout(jJungPanel);
        jJungPanel.setLayout(jJungPanelLayout);
        jJungPanelLayout.setHorizontalGroup(
            jJungPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 670, Short.MAX_VALUE)
        );
        jJungPanelLayout.setVerticalGroup(
            jJungPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 463, Short.MAX_VALUE)
        );

        jAddImporterButton.setText("+");
        jAddImporterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAddImporterButtonActionPerformed(evt);
            }
        });

        jReloadImportersButton.setText("Reload");
        jReloadImportersButton.setFocusable(false);

        jImporterScrollPane.setViewportView(jXImportersContainer);

        javax.swing.GroupLayout jImportersPanelLayout = new javax.swing.GroupLayout(jImportersPanel);
        jImportersPanel.setLayout(jImportersPanelLayout);
        jImportersPanelLayout.setHorizontalGroup(
            jImportersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jImportersPanelLayout.createSequentialGroup()
                .addComponent(jAddImporterButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jImporterSelectorComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 106, Short.MAX_VALUE)
                .addComponent(jReloadImportersButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jImportersPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jImporterScrollPane)
                .addContainerGap())
        );
        jImportersPanelLayout.setVerticalGroup(
            jImportersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jImportersPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jImporterScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jImportersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jAddImporterButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jReloadImportersButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jImporterSelectorComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
            .addComponent(jSettingsTabs, javax.swing.GroupLayout.Alignment.TRAILING)
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, 670, Short.MAX_VALUE)
            .addComponent(jJungPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addComponent(jJungPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    @SuppressWarnings("unchecked")
    private void jAddImporterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAddImporterButtonActionPerformed
        String name = (String) jImporterSelectorComboBox.getSelectedItem();
        String klassName = importerImpls.get(name);
        Properties properties = new Properties();
        addImporterView(klassName, properties);
        controller.getSettings().getImporters().put(klassName, properties);
    }//GEN-LAST:event_jAddImporterButtonActionPerformed

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
    private org.jdesktop.swingx.JXButton jAddImporterButton;
    private javax.swing.JPanel jAdvancedPanel;
    private javax.swing.JToggleButton jButtonClusters;
    private javax.swing.JToggleButton jButtonSources;
    private javax.swing.JToggleButton jCloudButton;
    private javax.swing.JScrollPane jImporterScrollPane;
    private javax.swing.JComboBox jImporterSelectorComboBox;
    private javax.swing.JPanel jImportersPanel;
    private uk.me.fommil.zibaldone.desktop.JungGraphView jJungPanel;
    private org.jdesktop.swingx.JXButton jReloadImportersButton;
    private org.jdesktop.swingx.JXSearchField jSearch;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JPanel jSettingsPanel;
    private javax.swing.JTabbedPane jSettingsTabs;
    private javax.swing.JPanel jSynonymsPanel;
    private javax.swing.JToolBar jToolBar;
    private org.jdesktop.swingx.JXTaskPaneContainer jXImportersContainer;
    // End of variables declaration//GEN-END:variables
}
