/*
 * Created 11-Jul-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import javax.swing.JLabel;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Tag;

/**
 *
 * @author Samuel Halliday
 */
public class NoteView extends javax.swing.JPanel {

    private Note note;
    
    public NoteView() {
        initComponents();
    }
    
    /**
     * @param note
     */
    public void setNote(Note note) {
        title.setText(note.getTitle());
        for (Tag tag : note.getTags()) {
            // TODO: resuse more generic TagView
            tags.add(new JLabel(tag.getText()));
        }
        contents.setText(note.getContents());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        title = new javax.swing.JLabel();
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        tags = new javax.swing.JPanel();
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        contents = new javax.swing.JTextArea();

        setLayout(new java.awt.BorderLayout());

        title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        title.setText("Title");
        add(title, java.awt.BorderLayout.NORTH);

        jPanel1.setLayout(new java.awt.BorderLayout());

        tags.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        jPanel1.add(tags, java.awt.BorderLayout.NORTH);

        contents.setColumns(20);
        contents.setRows(5);
        jScrollPane1.setViewportView(contents);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea contents;
    private javax.swing.JPanel tags;
    private javax.swing.JLabel title;
    // End of variables declaration//GEN-END:variables
}
