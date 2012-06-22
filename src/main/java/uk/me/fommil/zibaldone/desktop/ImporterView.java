/*
 * Created 31-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JPanel;
import uk.me.fommil.zibaldone.Importer;

/**
 * The specialist GUI widget for interacting with {@link Importer}s.
 *
 * @author Samuel Halliday
 */
public class ImporterView extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    private final ImporterController controller;

    private final AtomicBoolean locked;

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
        initComponents();
        this.controller = controller;
        locked = new AtomicBoolean(used);

        jBeanEditor.setBean(controller.getImporter().getSettings());

        if (used) {
            lockDownSpecial();
        }
    }

    // use GUI and get parent
//    // bit of a hack to remove oneself from a container
//    public void addSelfRemovalListener(final ActionListener listener) {
//        jXRemoveButton.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent ae) {
//                controller.getImporterController().doRemove(klass, properties);
//
//                listener.actionPerformed(new ActionEvent(this, 0, "self removal"));
//            }
//        });
//    }
    @Override
    public Dimension getMaximumSize() {
        Dimension preferred = getPreferredSize();
        preferred.width = Integer.MAX_VALUE;
        return preferred;
    }

    // refuse changes to "special" settings
    private void lockDownSpecial() {
        jBeanEditor.getBeanHelper().addVetoableChangeListener(new VetoableChangeListener() {

            @Override
            public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
                if (controller.isSpecial(evt.getPropertyName())) {
                    throw new PropertyVetoException(evt.getPropertyName() + " is special", evt);
                }
            }
        });
    }

    /**
     * WARNING: Do NOT modify this code.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar = new javax.swing.JToolBar();
        jXReloadButton = new org.jdesktop.swingx.JXButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 25), new java.awt.Dimension(0, 1000));
        jXRemoveButton = new org.jdesktop.swingx.JXButton();
        jBeanEditor = new uk.me.fommil.beans.JBeanEditor();

        setLayout(new java.awt.BorderLayout());

        jToolBar.setFloatable(false);
        jToolBar.setOrientation(1);

        jXReloadButton.setText("Reload");
        jXReloadButton.setFocusable(false);
        jXReloadButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jXReloadButton.setMaximumSize(new java.awt.Dimension(54, 18));
        jXReloadButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jXReloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXReloadButtonActionPerformed(evt);
            }
        });
        jToolBar.add(jXReloadButton);
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

        add(jToolBar, java.awt.BorderLayout.EAST);
        add(jBeanEditor, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jXRemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXRemoveButtonActionPerformed
        controller.doRemove();
        JPanel parent = (JPanel) getParent();
        parent.remove(this);
        parent.revalidate();
    }//GEN-LAST:event_jXRemoveButtonActionPerformed

    private void jXReloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXReloadButtonActionPerformed
        controller.doImport();
        if (!locked.getAndSet(true)) {
            lockDownSpecial();
        }
        for (String name : controller.getImporter().getSpecialPropertyNames()) {
            jBeanEditor.getBeanHelper().getProperty(name).setExpert(true);
        }
        jBeanEditor.refresh();
    }//GEN-LAST:event_jXReloadButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private uk.me.fommil.beans.JBeanEditor jBeanEditor;
    private javax.swing.JToolBar jToolBar;
    private org.jdesktop.swingx.JXButton jXReloadButton;
    private org.jdesktop.swingx.JXButton jXRemoveButton;
    // End of variables declaration//GEN-END:variables
}
