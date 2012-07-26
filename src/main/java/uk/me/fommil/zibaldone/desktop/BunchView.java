/*
 * Created 13-Jul-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.awt.Dimension;
import java.util.Set;
import lombok.extern.java.Log;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import uk.me.fommil.zibaldone.Bunch;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Tag;

/**
 * @author Samuel Halliday
 */
@Log
public class BunchView extends javax.swing.JPanel {

    private Bunch bunch;

    public BunchView() {
        initComponents();

        // TODO: a Remove Note button
        // TODO: Note popups

        tags.setWidth(400);
    }

    /**
     * @return the bunch with fields updated by the user input
     */
    public Bunch getBunch() {
        Preconditions.checkState(bunch != null);

        bunch.setName(name.getText());
        bunch.setContents(content.getText());

        return bunch;
    }

    /**
     * @param bunch
     */
    public void setBunch(final Bunch bunch) {
        Preconditions.checkNotNull(bunch);
        this.bunch = bunch;

        name.setText(bunch.getName());
        content.setText(bunch.getContents());

        Set<Note> allNotes = bunch.getNotes();
        Set<Tag> allTags = Sets.newTreeSet();
        Set<String> noteTitles = Sets.newTreeSet();
        for (Note note : allNotes) {
            allTags.addAll(note.getTags());
            noteTitles.add(note.getTitle());
        }
        tags.tagsAdded(allTags);
        notes.setModel(new ListComboBoxModel<String>(Lists.newArrayList(noteTitles)));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        name = new javax.swing.JTextField();
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        tags = new uk.me.fommil.zibaldone.desktop.TagsView();
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        content = new javax.swing.JEditorPane();
        javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
        notes = new javax.swing.JList();

        setLayout(new java.awt.BorderLayout());

        name.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        name.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        name.setText("Bunch Name");
        name.setBorder(null);
        add(name, java.awt.BorderLayout.PAGE_START);

        jPanel1.setMaximumSize(new java.awt.Dimension(800, 2147483647));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setBackground(null);

        tags.setBackground(null);
        tags.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jPanel2.add(tags);

        jPanel1.add(jPanel2, java.awt.BorderLayout.NORTH);

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(400, 300));

        content.setBorder(javax.swing.BorderFactory.createTitledBorder("Comments"));
        jScrollPane1.setViewportView(content);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("Notes"));
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setMaximumSize(new java.awt.Dimension(250, 32767));
        jScrollPane2.setMinimumSize(new java.awt.Dimension(100, 200));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(250, 200));

        notes.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jScrollPane2.setViewportView(notes);

        jPanel1.add(jScrollPane2, java.awt.BorderLayout.EAST);

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JEditorPane content;
    javax.swing.JTextField name;
    javax.swing.JList notes;
    uk.me.fommil.zibaldone.desktop.TagsView tags;
    // End of variables declaration//GEN-END:variables
}
