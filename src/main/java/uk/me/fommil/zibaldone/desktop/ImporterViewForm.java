/*
 * Created 31-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import javax.swing.JPanel;
import org.jdesktop.swingx.JXButton;

/**
 * The parts of {@link ImporterView} that can be set by the Netbeans GUI Editor.
 *
 * @author Samuel Halliday
 */
public class ImporterViewForm extends javax.swing.JPanel {

    public ImporterViewForm() {
        initComponents();
    }

    // <editor-fold defaultstate="collapsed" desc="BOILERPLATE GETTERS/SETTERS">
    public JPanel getjPropertiesPanel() {
        return jPropertiesPanel;
    }

    public JXButton getjXReloadButton() {
        return jXReloadButton;
    }

    public JXButton getjXRemoveButton() {
        return jXRemoveButton;
    }
    // </editor-fold>

    /**
     * WARNING: Do NOT modify this code.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPropertiesPanel = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jToolBar1 = new javax.swing.JToolBar();
        jXReloadButton = new org.jdesktop.swingx.JXButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 25), new java.awt.Dimension(0, 1000));
        jXRemoveButton = new org.jdesktop.swingx.JXButton();

        setLayout(new java.awt.BorderLayout());

        jPropertiesPanel.setLayout(new java.awt.BorderLayout());

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPropertiesPanel.add(jSeparator1, java.awt.BorderLayout.EAST);

        add(jPropertiesPanel, java.awt.BorderLayout.CENTER);

        jToolBar1.setFloatable(false);
        jToolBar1.setOrientation(1);

        jXReloadButton.setText("Reload");
        jXReloadButton.setFocusable(false);
        jXReloadButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jXReloadButton.setMaximumSize(new java.awt.Dimension(54, 18));
        jXReloadButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jXReloadButton);
        jToolBar1.add(filler1);

        jXRemoveButton.setText("Remove");
        jXRemoveButton.setFocusable(false);
        jXRemoveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jXRemoveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jXRemoveButton);

        add(jToolBar1, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.JPanel jPropertiesPanel;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private org.jdesktop.swingx.JXButton jXReloadButton;
    private org.jdesktop.swingx.JXButton jXRemoveButton;
    // End of variables declaration//GEN-END:variables
}
