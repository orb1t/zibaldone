/*
 * Created 27-Jun-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.swing;

import com.google.common.base.Preconditions;
import java.awt.Component;
import javax.annotation.Nullable;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Convenience methods for Swing UIs.
 * 
 * @author Samuel Halliday
 */
public final class SwingConvenience {

    /**
     * Much cleaner API than {@link JOptionPane#showMessageDialog(Component, Object, String, int)}.
     *
     * @param parent
     * @param warning
     */
    public static void warning(@Nullable final Component parent, final String warning) {
        Preconditions.checkNotNull(warning);
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JOptionPane.showMessageDialog(parent, warning, "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
    }
}
