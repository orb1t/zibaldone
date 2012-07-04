/*
 * Created 04-Jul-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import java.awt.FlowLayout;
import java.util.Set;
import javax.swing.JPanel;
import uk.me.fommil.zibaldone.Tag;

/**
 * Shows tags for the user to select.
 *
 * @author Samuel Halliday
 */
public class TagSelectView extends JPanel {

    public TagSelectView() {
        super();
        setLayout(new FlowLayout());
    }

    public void setTags(Set<Tag> tags) {
    }
}
