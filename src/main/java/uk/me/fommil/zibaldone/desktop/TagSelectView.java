/*
 * Created 04-Jul-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.base.Preconditions;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.Collection;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JPanel;
import lombok.Setter;
import lombok.extern.java.Log;
import uk.me.fommil.zibaldone.Tag;

/**
 * Shows tags for the user to select.
 *
 * @author Samuel Halliday
 */
@Log
public class TagSelectView extends JPanel {

    private class TagView extends JLabel {

        public TagView(Tag label) {
            super(label.getText());
            setBackground(Color.RED);
        }
    }

    @Setter
    private JungMainController controller;

    public TagSelectView() {
        super();
        setLayout(new FlowLayout());
        setBackground(Color.WHITE);
    }

    public void setTags(Collection<Tag> tags) {
        Preconditions.checkNotNull(tags);

        // TODO: only update the changed tags
        removeAll();
        for (Tag tag : tags) {
            log.info(tag.getText());
            add(new TagView(tag));
        }
        revalidate();
    }
}
