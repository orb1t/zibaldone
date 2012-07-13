/*
 * Created 31-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import java.awt.Dimension;
import java.io.IOException;
import java.util.logging.Level;
import javax.swing.JPanel;
import lombok.extern.java.Log;
import org.jdesktop.swingx.JXTaskPane;
import uk.me.fommil.swing.SwingConvenience;
import uk.me.fommil.zibaldone.Importer;

/**
 * The specialist GUI widget for interacting with {@link Importer}s.
 *
 * @author Samuel Halliday
 */
@Log
public class ImporterView extends JXTaskPane {

    private final ImporterController controller;

    /**
     * @deprecated exists only for GUI Editors
     */
    @Deprecated
    public ImporterView() {
        this(null, false);
    }

    /**
     * @param controller
     * @param used {@code true} if this importer has been previously used 
     */
    public ImporterView(ImporterController controller, boolean used) {
        super();
        initComponents();
        setCollapsed(used);
        setTitle(controller.getImporter().getName());

        this.controller = controller;

        jBeanEditor.setBean(controller.getImporter().getSettings());
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension preferred = getPreferredSize();
        preferred.width = Integer.MAX_VALUE;
        return preferred;
    }

    /**
     * WARNING: Do NOT modify this code.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        javax.swing.JToolBar jToolBar = new javax.swing.JToolBar();
        reloadButton = new org.jdesktop.swingx.JXButton();
        javax.swing.Box.Filler filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 25), new java.awt.Dimension(0, 1000));
        org.jdesktop.swingx.JXButton jXRemoveButton = new org.jdesktop.swingx.JXButton();
        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        jBeanEditor = new uk.me.fommil.beans.JBeanEditor();
        javax.swing.JSeparator jSeparator1 = new javax.swing.JSeparator();

        jPanel1.setLayout(new java.awt.BorderLayout());

        jToolBar.setFloatable(false);
        jToolBar.setOrientation(1);

        reloadButton.setText("Load");
        reloadButton.setFocusable(false);
        reloadButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        reloadButton.setMaximumSize(new java.awt.Dimension(54, 18));
        reloadButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        reloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadButtonActionPerformed(evt);
            }
        });
        jToolBar.add(reloadButton);
        jToolBar.add(filler1);

        jXRemoveButton.setText("Remove");
        jXRemoveButton.setFocusable(false);
        jXRemoveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jXRemoveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jXRemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXRemoveButtonActionPerformed(evt);
            }
        });
        jToolBar.add(jXRemoveButton);

        jPanel1.add(jToolBar, java.awt.BorderLayout.EAST);

        jPanel2.setLayout(new java.awt.BorderLayout());
        jPanel2.add(jBeanEditor, java.awt.BorderLayout.CENTER);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel2.add(jSeparator1, java.awt.BorderLayout.EAST);

        jPanel1.add(jPanel2, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jXRemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXRemoveButtonActionPerformed
        controller.doRemove();
        JPanel parent = (JPanel) getParent();
        parent.remove(this);
        parent.revalidate();
    }//GEN-LAST:event_jXRemoveButtonActionPerformed

    private void reloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadButtonActionPerformed
        try {
            // TODO: visual "wait" feedback
            controller.doImport();
            reloadButton.setText("Reload");
        } catch (IOException ex) {
            log.log(Level.WARNING, "failed import", ex);
            SwingConvenience.warning(this, "There was a problem with the data source.");
        }
    }//GEN-LAST:event_reloadButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private uk.me.fommil.beans.JBeanEditor jBeanEditor;
    private org.jdesktop.swingx.JXButton reloadButton;
    // End of variables declaration//GEN-END:variables
}
