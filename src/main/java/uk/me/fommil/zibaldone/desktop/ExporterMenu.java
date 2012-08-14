/*
 * Created 14-Aug-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.base.Preconditions;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import lombok.Setter;
import uk.me.fommil.zibaldone.Exporter;

/**
 * A dynamic {@link JMenu} that shows the available {@link Exporter}s and
 * provides a callback for selected entries.
 * 
 * @author Samuel Halliday
 */
public class ExporterMenu extends JMenu {

    public interface Callback {

        /**
         * @param exporter
         */
        public void selectedExporter(Exporter exporter);
    }

    @Setter
    private Callback callback;

    private final JMenuItem none = new JMenuItem("empty");

    {
        none.setEnabled(false);
        add(none);
    }

    public void setExporters(Collection<Exporter> exporters) {
        Preconditions.checkNotNull(exporters);
        removeAll();

        for (final Exporter exporter : exporters) {
            JMenuItem item = new JMenuItem(exporter.getName());
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (callback != null) {
                        callback.selectedExporter(exporter);
                    }
                }
            });
            add(item);
        }

        if (getMenuComponentCount() == 0) {
            add(none);
        }
    }
}
