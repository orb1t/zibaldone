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
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import uk.me.fommil.swing.WrapLayout;
import uk.me.fommil.zibaldone.Tag;
import uk.me.fommil.zibaldone.desktop.JungMainController.TagChoice;
import uk.me.fommil.zibaldone.desktop.JungMainController.TagsChangedListener;

/**
 * Shows tags and provides programmatic options to enable tag selection,
 * deletion and creation.
 * <p>
 * TODO: implement editable mode
 * 
 * @author Samuel Halliday
 */
@Log
public class TagsView extends JPanel implements TagsChangedListener {

    private final class TagView extends JLabel {

        private final Tag tag;

        private TagChoice choice;

        public TagView(Tag tag, TagChoice choice) {
            super(tag.getText());
            Preconditions.checkNotNull(tag);
            Preconditions.checkNotNull(choice);
            setLayout(new WrapLayout());
            this.tag = tag;
            setOpaque(true);
            setChoice(choice);
            // TODO: font changes to TagsView after instantiation are ignored
            setFont(TagsView.this.getFont());
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    click();
                }
            });
        }

        private void click() {
            if (!selectable) {
                return;
            }
            setChoice(nextChoice());
            controller.selectTag(tag, choice);
        }

        public void setChoice(TagChoice choice) {
            Preconditions.checkNotNull(choice);
            if (this.choice == choice) {
                return;
            }
            this.choice = choice;
            // TODO: rounded borders for tag background
            setBackground(getColorForChoice(choice));
            repaint();
        }

        private TagChoice nextChoice() {
            switch (choice) {
                case IGNORE:
                    return TagChoice.SHOW;
                case SHOW:
                    return TagChoice.HIDE;
                default:
                    return TagChoice.IGNORE;
            }
        }

        private Color getColorForChoice(TagChoice choice) {
            switch (choice) {
                case IGNORE:
                    return null;
                case SHOW:
                    return Color.GREEN;
                default:
                    return Color.RED;
            }
        }
    }

    @Setter
    private JungMainController controller;

    // TODO: editable/selectable should be listened to
    @Getter @Setter
    private boolean editable;

    @Getter @Setter
    private boolean selectable;

    public TagsView() {
        super(new FlowLayout());
    }

    private final SortedMap<Tag, TagView> views = Maps.newTreeMap();

    public void setTags(Set<Tag> tags) {
        Preconditions.checkNotNull(tags);

        for (Tag tag : tags) {
            if (!views.containsKey(tag)) {
                TagView view = new TagView(tag, TagChoice.IGNORE);
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
    }

    @Override
    public void tagsChanged(Set<Tag> tags) {
        setTags(tags);
        revalidate();
        repaint();
    }

    @Override
    public void tagSelectionChanged(Multimap<TagChoice, Tag> selection) {
        Preconditions.checkNotNull(selection);
        Preconditions.checkState(selectable, "not selectable");

        Collection<Tag> selected = selection.values();

        for (Tag tag : views.keySet()) {
            if (!selected.contains(tag)) {
                views.get(tag).setChoice(TagChoice.IGNORE);
            }
        }
        for (Entry<TagChoice, Tag> entry : selection.entries()) {
            Preconditions.checkState(views.containsKey(entry.getValue()));
            views.get(entry.getValue()).setChoice(entry.getKey());
        }

        // no layout changes, so no revalidation needed
    }
}
