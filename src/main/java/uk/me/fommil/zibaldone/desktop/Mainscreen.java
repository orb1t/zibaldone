/*
 * Created 24-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import uk.me.fommil.zibaldone.control.GraphController;
import com.google.common.base.Preconditions;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditorManager;
import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import javax.persistence.EntityManagerFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import lombok.BoundSetter;
import lombok.extern.java.Log;
import org.jdesktop.swingx.combobox.MapComboBoxModel;
import uk.me.fommil.beans.editors.DatePropertyEditor;
import uk.me.fommil.beans.editors.FilePropertyEditor;
import uk.me.fommil.persistence.CrudDao;
import uk.me.fommil.swing.SwingConvenience;
import uk.me.fommil.zibaldone.Exporter;
import uk.me.fommil.zibaldone.Importer;
import uk.me.fommil.zibaldone.control.BunchController;
import uk.me.fommil.zibaldone.control.ExporterController;
import uk.me.fommil.zibaldone.control.ImporterController;
import uk.me.fommil.zibaldone.control.Settings;
import uk.me.fommil.zibaldone.control.TagController;

/**
 * @author Samuel Halliday
 */
@Log
public final class Mainscreen extends JFrame implements PropertyChangeListener {
    
    public static void main(String args[]) throws Exception {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        
        PropertyEditorManager.registerEditor(File.class, FilePropertyEditor.class);
        PropertyEditorManager.registerEditor(Date.class, DatePropertyEditor.class);
        
        final EntityManagerFactory emf = CrudDao.createEntityManagerFactory("ZibaldonePU");
        final Settings settings = Settings.loadAutoSavingInstance(new File("zibaldone.xml"));
                
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GraphController graphController = new GraphController(emf, settings);
                TagController tagController = new TagController(settings);
                BunchController bunchController = new BunchController(emf, settings);
                ImporterController importerController = new ImporterController(emf, settings);
                ExporterController exporterController = new ExporterController();
                importerController.addTagListener(graphController);
                importerController.addNoteListener(graphController);
                tagController.addTagListener(graphController);
                
                Mainscreen main = new Mainscreen();
                main.setSettings(settings);
                main.setTagController(tagController);
                main.setGraphController(graphController);
                main.setBunchController(bunchController);
                main.setImporterController(importerController);
                main.setExporterController(exporterController);
                
                main.setVisible(true);
                
                importerController.loadDb();
                bunchController.loadDb();
                
                SwingConvenience.setUncaughtExceptionHandlerPopup(main);
            }
        });
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
    private ExporterController exporterController;
    
    @BoundSetter
    private Settings settings;
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Preconditions.checkNotNull(evt);
        String property = evt.getPropertyName();
        log.fine("Changed " + property);
        if ("graphController".equals(property)) {
            Preconditions.checkState(jungGraphView != null);
            GraphController old = (GraphController) evt.getOldValue();
            if (old != null) {
                old.removeClusterListener(jungGraphView);
            }
            graphController.addClusterListener(jungGraphView);
            jungGraphView.setGraph(graphController.getGraph());
            relatorMenu.setGraphController(graphController);
        } else if ("tagController".equals(property)) {
            Preconditions.checkState(tagSelectView != null);
            TagController old = (TagController) evt.getOldValue();
            if (old != null) {
                old.removeTagListener(tagSelectView);
            }
            tagController.addTagListener(tagSelectView);
            tagSelectView.setTagController(tagController);
        } else if ("bunchController".equals(property)) {
            Preconditions.checkState(jungGraphView != null);
            Preconditions.checkState(bunchMenu != null);
            BunchController old = (BunchController) evt.getOldValue();
            if (old != null) {
                old.removeBunchListener(jungGraphView);
                old.removeBunchListener(bunchMenu);
            }
            bunchController.addBunchListener(jungGraphView);
            bunchController.addBunchListener(bunchMenu);
            jungGraphView.setBunchController(bunchController);
            bunchMenu.setBunchController(bunchController);
            exporterView.setBunchController(bunchController);
        } else if ("importerController".equals(property)) {
            Preconditions.checkState(tagSelectView != null);
            ImporterController old = (ImporterController) evt.getOldValue();
            if (old != null) {
                importerController.removeTagListener(tagSelectView);
            }
            importerController.addTagListener(tagSelectView);
            Map<String, Class<Importer>> importers = importerController.getImporterImplementations();
            ComboBoxModel importerChoices = new MapComboBoxModel<String, Class<Importer>>(importers);
            importerSelector.setModel(importerChoices);
        } else if ("exporterController".equals(property)) {
            exportMenu.setExporters(exporterController.getExporterImplementations());
            exportMenu.setCallback(new ExporterMenu.Callback() {
                @Override
                public void selectedExporter(Exporter exporter) {
                    exporterActionPerformed(exporter);
                }
            });
        } else if ("settings".equals(property)) {
            for (Component component : importersPanel.getComponents()) {
                if (component instanceof ImporterView) {
                    importersPanel.remove(component);
                }
            }
            for (UUID uuid : settings.getImporters().keySet()) {
                addImporter(uuid, true);
            }
            search.setText(settings.getSearch());
            relatorMenu.setSettings(settings);
        }
    }
    
    public Mainscreen() {
        super();
        rootPane.putClientProperty("apple.awt.brushMetalLook", Boolean.TRUE);
        SwingConvenience.enableOSXFullscreen(this);
        
        initComponents();
        addPropertyChangeListener(this);
    }
    
    private void addImporter(UUID uuid, boolean used) {
        ImporterView importerView = new ImporterView();
        importerView.setImporterController(importerController);
        importerView.setSettings(settings);
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
        importDialog = new javax.swing.JDialog();
        javax.swing.JPanel jImportersPanel = new javax.swing.JPanel();
        javax.swing.JToolBar jToolBar1 = new javax.swing.JToolBar();
        org.jdesktop.swingx.JXButton jAddImporterButton = new org.jdesktop.swingx.JXButton();
        importerSelector = new javax.swing.JComboBox();
        javax.swing.Box.Filler filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(500, 32767));
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        importersPanel = new org.jdesktop.swingx.JXTaskPaneContainer();
        exportDialog = new javax.swing.JDialog();
        exporterView = new uk.me.fommil.zibaldone.desktop.ExporterView();
        javax.swing.JToolBar jToolBar = new javax.swing.JToolBar();
        search = new org.jdesktop.swingx.JXSearchField();
        javax.swing.JButton tagsButton = new javax.swing.JButton();
        javax.swing.Box.Filler filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(50, 0), new java.awt.Dimension(1000, 0));
        jungGraphView = new uk.me.fommil.zibaldone.desktop.JungGraphView();
        javax.swing.JMenuBar menu = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem importMenu = new javax.swing.JMenuItem();
        exportMenu = new uk.me.fommil.zibaldone.desktop.ExporterMenu();
        bunchMenu = new uk.me.fommil.zibaldone.desktop.BunchMenu();
        relatorMenu = new uk.me.fommil.zibaldone.desktop.RelatorMenu();

        tagDialog.setTitle("Tags");
        tagDialog.setAlwaysOnTop(true);
        tagDialog.setResizable(false);

        tagSelectView.setSelectable(true);
        tagSelectView.setWidth(400);
        tagDialog.getContentPane().add(tagSelectView, java.awt.BorderLayout.CENTER);

        importDialog.setTitle("Importers");
        importDialog.setAlwaysOnTop(true);
        importDialog.setModal(true);
        importDialog.setResizable(false);

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

        jImportersPanel.add(jToolBar1, java.awt.BorderLayout.SOUTH);

        jScrollPane1.setBorder(null);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(320, 480));
        jScrollPane1.setViewportView(null);
        jScrollPane1.setViewportView(importersPanel);

        jImportersPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        importDialog.getContentPane().add(jImportersPanel, java.awt.BorderLayout.CENTER);

        exportDialog.setTitle("Export");
        exportDialog.setAlwaysOnTop(true);
        exportDialog.setModal(true);
        exportDialog.setResizable(false);
        exportDialog.getContentPane().add(exporterView, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Zibaldone");
        setMinimumSize(new java.awt.Dimension(900, 600));
        setSize(new java.awt.Dimension(800, 600));

        jToolBar.setFloatable(false);
        jToolBar.setRollover(true);
        jToolBar.setPreferredSize(new java.awt.Dimension(480, 42));

        search.setMaximumSize(new java.awt.Dimension(1000, 2147483647));
        search.setMinimumSize(new java.awt.Dimension(100, 28));
        search.setPrompt("Search titles, tags and contents");
        search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchActionPerformed(evt);
            }
        });
        search.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                searchKeyTyped(evt);
            }
        });
        jToolBar.add(search);

        tagsButton.setText("Tags");
        tagsButton.setFocusable(false);
        tagsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tagsButtonActionPerformed(evt);
            }
        });
        jToolBar.add(tagsButton);
        jToolBar.add(filler1);

        getContentPane().add(jToolBar, java.awt.BorderLayout.PAGE_START);
        getContentPane().add(jungGraphView, java.awt.BorderLayout.CENTER);

        fileMenu.setText("File");

        importMenu.setText("Import");
        importMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importMenuActionPerformed(evt);
            }
        });
        fileMenu.add(importMenu);

        exportMenu.setText("Export");
        fileMenu.add(exportMenu);

        menu.add(fileMenu);

        bunchMenu.setText("Bunches");
        menu.add(bunchMenu);

        relatorMenu.setText("Relators");
        menu.add(relatorMenu);

        setJMenuBar(menu);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @SuppressWarnings("unchecked")
    private void jAddImporterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAddImporterButtonActionPerformed
        String name = (String) importerSelector.getSelectedItem();
        Entry<UUID, Importer> pair = importerController.newImporter(name);
        settings.getImporters().put(pair.getKey(), pair.getValue());
        addImporter(pair.getKey(), false);
    }//GEN-LAST:event_jAddImporterButtonActionPerformed
    
    private void tagsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tagsButtonActionPerformed
        tagDialog.pack();
        SwingConvenience.relocateDialogAtMouse(tagDialog);
        tagDialog.setVisible(true);
    }//GEN-LAST:event_tagsButtonActionPerformed
    
    private void searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchActionPerformed
        graphController.searchChanged(search.getText());
    }//GEN-LAST:event_searchActionPerformed
    
    private void searchKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchKeyTyped
        char c = evt.getKeyChar();
        if (c == KeyEvent.VK_ENTER || c == KeyEvent.VK_TAB) {
            jungGraphView.requestFocus();
        }
    }//GEN-LAST:event_searchKeyTyped
    
    private void importMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importMenuActionPerformed
        importDialog.pack();
        SwingConvenience.relocateDialogAtMouse(importDialog);
        importDialog.setVisible(true);
    }//GEN-LAST:event_importMenuActionPerformed
    
    private void exporterActionPerformed(Exporter exporter) {
        exporterView.setExporter(exporter);
        exportDialog.pack();
        SwingConvenience.relocateDialogAtMouse(exportDialog);
        exportDialog.setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    uk.me.fommil.zibaldone.desktop.BunchMenu bunchMenu;
    javax.swing.JDialog exportDialog;
    uk.me.fommil.zibaldone.desktop.ExporterMenu exportMenu;
    uk.me.fommil.zibaldone.desktop.ExporterView exporterView;
    javax.swing.JDialog importDialog;
    javax.swing.JComboBox importerSelector;
    org.jdesktop.swingx.JXTaskPaneContainer importersPanel;
    uk.me.fommil.zibaldone.desktop.JungGraphView jungGraphView;
    private uk.me.fommil.zibaldone.desktop.RelatorMenu relatorMenu;
    org.jdesktop.swingx.JXSearchField search;
    javax.swing.JDialog tagDialog;
    uk.me.fommil.zibaldone.desktop.TagsView tagSelectView;
    // End of variables declaration//GEN-END:variables
}
