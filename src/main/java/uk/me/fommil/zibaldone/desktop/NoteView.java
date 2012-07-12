/*
 * Created 11-Jul-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import uk.me.fommil.zibaldone.Note;

/**
 * Popup view of a Note.
 * 
 * TODO: use an HTML rendered version of the contents instead of a JTextArea
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
        tags.setTags(note.getTags());
        contents.setText(note.getContents());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        title = new javax.swing.JLabel();
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        contents = new javax.swing.JTextArea();
        tags = new uk.me.fommil.zibaldone.desktop.TagsView();

        setMinimumSize(new java.awt.Dimension(100, 50));
        setLayout(new java.awt.BorderLayout());

        title.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        title.setText("Title");
        add(title, java.awt.BorderLayout.NORTH);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBackground(null);
        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        contents.setEditable(false);
        contents.setBackground(null);
        contents.setColumns(30);
        contents.setLineWrap(true);
        contents.setRows(10);
        contents.setWrapStyleWord(true);
        jScrollPane1.setViewportView(contents);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        tags.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jPanel1.add(tags, java.awt.BorderLayout.PAGE_START);

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JTextArea contents;
    uk.me.fommil.zibaldone.desktop.TagsView tags;
    javax.swing.JLabel title;
    // End of variables declaration//GEN-END:variables
}
