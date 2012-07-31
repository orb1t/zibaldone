/*
 * Created 04-Jul-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;
import java.util.SortedMap;
import javax.swing.JLabel;
import javax.swing.JPanel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import uk.me.fommil.swing.WrapLayout;
import uk.me.fommil.zibaldone.Tag;
import uk.me.fommil.zibaldone.control.Listeners.TagListener;
import uk.me.fommil.zibaldone.control.TagController;
import uk.me.fommil.zibaldone.control.TagController.TagChoice;

/**
 * Shows tags and provides programmatic options to enable tag selection,
 * deletion and creation. The user sets the width of this and the height
 * is automatically calculated based on the tags and the inherited font.
 * <p>
 * TODO: implement editable mode
 * 
 * @author Samuel Halliday
 */
@Log
public class TagsView extends JPanel implements TagListener {

    private final class TagView extends JLabel {

        @Getter
        private final Tag tag;

        @Getter
        private TagChoice choice;

        // TODO: rounded borders for tag background
        public TagView(Tag tag, TagChoice choice) {
            super(tag.getText());
            setLayout(new WrapLayout());
            this.tag = tag;
            setOpaque(true);
            setChoice(Preconditions.checkNotNull(choice));
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
            tagController.selectTag(tag, nextChoice());
        }

        public void setChoice(TagChoice choice) {
            this.choice = Preconditions.checkNotNull(choice);
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
    private TagController tagController;

    // TODO: editable/selectable should be listened to
    @Getter @Setter
    private boolean editable;

    @Getter @Setter
    private boolean selectable;

    public TagsView() {
        super(new WrapLayout());
    }

    private final SortedMap<Tag, TagView> views = Maps.newTreeMap();

    @Override
    public void tagsAdded(Set<Tag> tags) {
        for (Tag tag : tags) {
            if (!views.containsKey(tag)) {
                TagView view = new TagView(tag, TagChoice.IGNORE);
                views.put(tag, view);
            }
        }
        redraw();
    }

    @Override
    public void tagsRemoved(Set<Tag> tags) {
        for (Tag tag : views.keySet()) {
            if (!tags.contains(tag)) {
                views.remove(tag);
            }
        }
        redraw();
    }

    @Override
    public void tagSelection(Tag tag, TagChoice choice) {
        Preconditions.checkNotNull(tag);
        Preconditions.checkNotNull(choice);

        views.get(tag).setChoice(choice);
    }

    private void redraw() {
        removeAll();
        for (TagView view : views.values()) {
            add(view);
        }
        revalidate();
        repaint();
    }

    /**
     * @param width
     */
    public void setWidth(int width) {
        setSize(width, getSize().height);
    }
}
