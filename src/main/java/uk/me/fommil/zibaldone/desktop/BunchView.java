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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Set;
import lombok.Getter;
import lombok.extern.java.Log;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import uk.me.fommil.zibaldone.Bunch;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Tag;

/**
 *
 * @author Samuel Halliday
 */
@Log
public class BunchView extends javax.swing.JPanel {
    
    @Getter
    private Bunch bunch;
    
    public BunchView() {
        initComponents();
    }
    
    public void setBunch(final Bunch bunch) {
        Preconditions.checkNotNull(bunch);
        Preconditions.checkState(this.bunch == null, "bunch cannot be rebound");
        this.bunch = bunch;
        name.setText(bunch.getName());
        
        Set<Note> allNotes = bunch.getNotes();
        Set<Tag> allTags = Sets.newTreeSet();
        Set<String> noteTitles = Sets.newTreeSet();
        for (Note note : allNotes) {
            allTags.addAll(note.getTags());
            noteTitles.add(note.getTitle());
        }
        log.info(allTags.toString());
        tags.setTags(allTags);
        notes.setModel(new ListComboBoxModel<String>(Lists.newArrayList(noteTitles)));

        // TODO: ensure changes to the bunch are persisted
        name.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                bunch.setName(name.getText());
            }
        });
        content.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                bunch.setContents(content.getText());
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        name = new javax.swing.JTextField();
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        content = new javax.swing.JEditorPane();
        tags = new uk.me.fommil.zibaldone.desktop.TagsView();
        javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
        notes = new javax.swing.JList();

        setLayout(new java.awt.BorderLayout());

        name.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        name.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        name.setText("Bunch Name");
        name.setBorder(null);
        add(name, java.awt.BorderLayout.PAGE_START);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setViewportView(content);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        tags.setPreferredSize(new java.awt.Dimension(400, 20));

        javax.swing.GroupLayout tagsLayout = new javax.swing.GroupLayout(tags);
        tags.setLayout(tagsLayout);
        tagsLayout.setHorizontalGroup(
            tagsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        tagsLayout.setVerticalGroup(
            tagsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        jPanel1.add(tags, java.awt.BorderLayout.NORTH);

        jScrollPane2.setBorder(null);
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setMaximumSize(new java.awt.Dimension(75, 32767));
        jScrollPane2.setMinimumSize(new java.awt.Dimension(50, 0));

        notes.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        notes.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
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
