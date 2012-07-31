/*
 * Created 27-Jul-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.collect.Maps;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.UUID;
import javax.swing.JCheckBox;
import javax.swing.JPopupMenu;
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
public class BunchMenu extends JPopupMenu implements Listeners.BunchListener {

    @Getter @Setter
    private BunchController bunchController;

    // Don't use CheckBoxMenuItem because of http://stackoverflow.com/questions/3759379
    private final Map<UUID, JCheckBox> entries = Maps.newTreeMap();

//    private static class StayOpenCheckBoxMenuItemUI extends BasicCheckBoxMenuItemUI {
//
//        @Override
//        protected void doClick(MenuSelectionManager msm) {
//            menuItem.doClick(0);
//        }
//    };
    @Override
    public void bunchAdded(Bunch bunch) {
        final UUID id = bunch.getId();
        final JCheckBox item = new JCheckBox(bunch.getName(), false);
//        item.setUI(new StayOpenCheckBoxMenuItemUI());
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
        entries.remove(bunch.getId());
        rebuild();
    }

    @Override
    public void bunchUpdated(Bunch bunch) {
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
        for (JCheckBox item : entries.values()) {
            add(item);
        }
    }
}
