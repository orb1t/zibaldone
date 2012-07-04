/*
 * Created 04-Jul-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JPanel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import uk.me.fommil.zibaldone.Tag;
import uk.me.fommil.zibaldone.desktop.JungMainController.TagChoice;

/**
 * Shows tags for the user to select.
 *
 * @author Samuel Halliday
 */
@Log
public class TagSelectView extends JPanel {

    private class TagView extends JLabel {

        private TagChoice selected = TagChoice.IGNORE;

        @Getter
        private final Tag tag;

        public TagView(Tag tag) {
            super(tag.getText());
            this.tag = tag;
            setOpaque(true);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    click();
                }
            });
        }

        public void click() {
            selected = TagChoice.values()[(1 + selected.ordinal()) % TagChoice.values().length];
            switch (selected) {
                case SHOW:
                    setBackground(Color.GREEN);
                    break;
                case HIDE:
                    setBackground(Color.RED);
                    break;
                default:
                    setBackground(null);
            }
            log.info(getBackground().toString());
            repaint();
            controller.selectTag(selected, tag);
        }
    }

    @Setter
    private JungMainController controller;

    public TagSelectView() {
        super(new FlowLayout());
    }

    // TODO: this should be receiving abstract atomic updates to Tags
    // TODO: should we show all tags or only "resolved" tags?
    public void setTags(List<Tag> tags) {
        Preconditions.checkNotNull(tags);

        for (Component view : getComponents()) {
            Preconditions.checkState(view instanceof TagView);
            Tag tag = ((TagView) view).getTag();
            if (!tags.contains(tag)) {
                remove(view);
            }
        }

        for (int i = 0; i < tags.size(); i++) {
            Tag tag = tags.get(i);
            if (getComponentCount() > i) {
                Component view = getComponent(i);
                Preconditions.checkState(view instanceof TagView);
                Tag shown = ((TagView) view).getTag();
                if (tag.equals(shown)) {
                    continue;
                }
            }
            TagView view = new TagView(tag);
            add(view, i);
        }
        validate();
    }
}
