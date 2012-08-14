/*
 * Created 27-Jul-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.UUID;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import uk.me.fommil.zibaldone.Bunch;
import uk.me.fommil.zibaldone.control.BunchController;
import uk.me.fommil.zibaldone.control.Listeners;
import uk.me.fommil.zibaldone.control.TagController.TagChoice;

/**
 * @author Samuel Halliday
 */
@Log
public class BunchMenu extends JMenu implements Listeners.BunchListener {

    @Getter @Setter
    private BunchController bunchController;

    // JCheckBoxMenuItem closes JMenu http://stackoverflow.com/questions/3759379
    private final Map<UUID, JCheckBoxMenuItem> entries = Maps.newTreeMap();

    private final JMenuItem none = new JMenuItem("No Bunches");

    public BunchMenu() {
        super();
        none.setEnabled(false);
        add(none);
    }

    @Override
    public void bunchAdded(Bunch bunch) {
        Preconditions.checkNotNull(bunch);

        remove(none);
        final UUID id = bunch.getId();
        final JCheckBoxMenuItem item = new JCheckBoxMenuItem(bunch.getName(), false);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // this is called after the 'selected' flag is changed,
                // which is J2SE breaking the MVC by holding M in the V
                TagChoice choice = TagChoice.SHOW;
                if (!item.isSelected()) {
                    choice = TagChoice.HIDE;
                }
                Bunch update = bunchController.getBunch(id);
                bunchController.selectBunch(update, choice);
            }
        });
        entries.put(id, item);
        rebuild();
    }

    @Override
    public void bunchRemoved(Bunch bunch) {
        Preconditions.checkNotNull(bunch);
        entries.remove(bunch.getId());
        rebuild();
    }

    @Override
    public void bunchUpdated(Bunch bunch) {
        Preconditions.checkNotNull(bunch);
        entries.get(bunch.getId()).setText(bunch.getName());
        rebuild();
    }

    @Override
    public void bunchSelectionChanged(Bunch bunch, TagChoice choice) {
        boolean selected = false;
        if (choice == TagChoice.SHOW) {
            selected = true;
        }
        entries.get(bunch.getId()).setSelected(selected);
    }

    private void rebuild() {
        removeAll();
        for (JCheckBoxMenuItem item : entries.values()) {
            add(item);
        }

        if (getMenuComponentCount() == 0) {
            add(none);
        }
    }
}
