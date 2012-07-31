/*
 * Created 27-Jun-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.swing;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import org.apache.commons.math3.stat.StatUtils;
import uk.me.fommil.zibaldone.Note;

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

    /**
     * Packs and then shows a popup menu at the mouse location, contained
     * within a Component.
     * 
     * @param popup
     * @param owner
     */
    public static void popupAtMouse(JPopupMenu popup, Component owner) {
        Preconditions.checkNotNull(popup);
        Preconditions.checkNotNull(owner);
        popup.pack();
        Point mouse = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(mouse, owner);
        // http://stackoverflow.com/questions/766956
        popup.show(owner, mouse.x, mouse.y);
    }

    /**
     * @param title
     * @param component
     * @param modal
     * @param listener 
     */
    public static void showAsDialog(String title, Component component, boolean modal, @Nullable WindowListener listener) {
        JDialog dialog = new JDialog();
        dialog.setTitle(Preconditions.checkNotNull(title));
        dialog.setModal(modal);
        dialog.add(Preconditions.checkNotNull(component));
        if (listener != null) {
            dialog.addWindowListener(listener);
        }
        dialog.pack();
        dialog.setVisible(true);
    }

    /**
     * @param points
     * @return the average of the given points
     */
    public static Point2D average(Collection<Point2D> points) {
        Preconditions.checkNotNull(points);
        Preconditions.checkArgument(!points.isEmpty());

        List<Double> xs = Lists.newArrayList();
        List<Double> ys = Lists.newArrayList();

        for (Point2D point : points) {
            xs.add(point.getX());
            ys.add(point.getY());
        }
        double x = StatUtils.mean(Doubles.toArray(xs));
        double y = StatUtils.mean(Doubles.toArray(ys));
        return new Point2D.Double(x, y);
    }
}
