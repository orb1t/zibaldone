/*
 * Created 14-Aug-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.base.Preconditions;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import javax.swing.Box.Filler;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.java.Log;
import uk.me.fommil.beans.JBeanEditor;
import uk.me.fommil.zibaldone.Bunch;
import uk.me.fommil.zibaldone.Exporter;
import uk.me.fommil.zibaldone.control.BunchController;

/**
 * View for an {@link Exporter}.
 * 
 * @author Samuel Halliday
 */
@Log
public class ExporterView extends JPanel {

    @Setter @NonNull
    private BunchController bunchController;

    private Exporter exporter;

    public ExporterView() {
        super();
        initComponents();
    }

    public void setExporter(Exporter exporter) {
        this.exporter = Preconditions.checkNotNull(exporter);

        JBeanEditor editor = new JBeanEditor();
        editor.setBean(exporter.getSettings());
        settingsPane.removeAll();
        settingsPane.add(editor, BorderLayout.CENTER);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        JToolBar jToolBar1 = new JToolBar();
        Filler filler1 = new Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        JButton jButton1 = new JButton();
        settingsPane = new JPanel();

        setLayout(new BorderLayout());

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.add(filler1);

        jButton1.setText("Export");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(SwingConstants.BOTTOM);
        jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);

        add(jToolBar1, BorderLayout.PAGE_END);

        settingsPane.setLayout(new BorderLayout());
        add(settingsPane, BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Collection<Bunch> bunches = bunchController.getBunches();
        try {
            exporter.export(bunches);
        } catch (IOException ex) {
            log.log(Level.WARNING, "Exporter FAIL", ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    JPanel settingsPane;
    // End of variables declaration//GEN-END:variables
}
