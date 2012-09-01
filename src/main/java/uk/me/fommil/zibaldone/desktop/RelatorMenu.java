/*
 * Created 01-Sep-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.base.Preconditions;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import lombok.Getter;
import lombok.Setter;
import uk.me.fommil.zibaldone.Relator;
import uk.me.fommil.zibaldone.control.GraphController;
import uk.me.fommil.zibaldone.control.Settings;

/**
 *
 * @author Samuel Halliday
 */
public class RelatorMenu extends JMenu {

    private final JMenuItem none = new JMenuItem("empty");

    @Getter
    private GraphController graphController;

    @Setter
    private Settings settings;

    {
        none.setEnabled(false);
        add(none);
    }

    /**
     * @param graphController
     */
    public void setGraphController(final GraphController graphController) {
        this.graphController = graphController;

        removeAll();

        if (graphController != null && settings != null) {
            String selected = settings.getSelectedRelator().getName();
            Map<String, Relator> relators = graphController.getRelators();
            ButtonGroup group = new ButtonGroup();
            for (Entry<String, Relator> entry : relators.entrySet()) {
                String name = entry.getKey();
                final Relator relator = entry.getValue();
                final JRadioButtonMenuItem item = new JRadioButtonMenuItem(name, selected.equals(name));
                group.add(item);

                item.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (item.isSelected()) {
                            graphController.selectRelator(relator);
                        }
                    }
                });
                add(item);
            }
        }

        if (getMenuComponentCount() == 0) {
            add(none);
        }
    }
}
