/*
 * Created 04-Jul-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import javax.swing.JLabel;
import javax.swing.JPanel;
import lombok.Setter;
import lombok.extern.java.Log;
import uk.me.fommil.zibaldone.Tag;
import uk.me.fommil.zibaldone.desktop.JungMainController.TagChoice;
import uk.me.fommil.zibaldone.desktop.JungMainController.TagsChangedListener;

/**
 * Shows tags for the user to select.
 *
 * @author Samuel Halliday
 */
@Log
public class TagSelectView extends JPanel implements TagsChangedListener {

    private final class TagView extends JLabel {

        private final Tag tag;

        private TagChoice choice;

        public TagView(Tag tag, TagChoice choice) {
            super(tag.getText());
            Preconditions.checkNotNull(tag);
            this.tag = tag;
            setOpaque(true);
            setChoice(choice);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    click();
                }
            });
        }

        public void click() {
            TagChoice newChoice = TagChoice.SHOW;
            if (choice != null) {
                switch (choice) {
                    case SHOW:
                        newChoice = TagChoice.HIDE;
                        break;
                    case HIDE:
                        newChoice = null;
                }
            }
            controller.selectTag(tag, newChoice);
        }

        public void setChoice(TagChoice choice) {
            if (choice == this.choice) {
                return;
            }
            this.choice = choice;
            if (choice == null) {
                setBackground(null);
            } else {
                switch (choice) {
                    case SHOW:
                        setBackground(Color.GREEN);
                        break;
                    case HIDE:
                        setBackground(Color.RED);
                }
            }
            repaint();
        }
    }

    @Setter
    private JungMainController controller;

    public TagSelectView() {
        super(new FlowLayout());
    }

    private final SortedMap<Tag, TagView> views = Maps.newTreeMap();

    @Override
    public void tagsChanged(Set<Tag> tags) {
        Preconditions.checkNotNull(tags);

        for (Tag tag : tags) {
            if (!views.containsKey(tag)) {
                TagView view = new TagView(tag, null);
                views.put(tag, view);
            }
        }
        for (Tag tag : views.keySet()) {
            if (!tags.contains(tag)) {
                views.remove(tag);
            }
        }
        removeAll();
        for (TagView view : views.values()) {
            add(view);
        }

        validate();
    }

    @Override
    public void tagSelectionChanged(Multimap<TagChoice, Tag> selection) {
        Preconditions.checkNotNull(selection);
        Collection<Tag> selected = selection.values();

        for (Tag tag : views.keySet()) {
            if (!selected.contains(tag)) {
                views.get(tag).setChoice(null);
            }
        }
        for (Entry<TagChoice, Tag> entry : selection.entries()) {
            Preconditions.checkState(views.containsKey(entry.getValue()));
            views.get(entry.getValue()).setChoice(entry.getKey());
        }
    }
}
