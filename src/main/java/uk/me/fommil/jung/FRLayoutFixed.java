/*
 * Created 30-Jul-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.jung;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.util.RandomLocationTransformer;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import lombok.extern.java.Log;
import org.apache.commons.collections15.map.LazyMap;

/**
 * Overrides {@link FRLayout} to fix a few bugs in JUNG.
 * 
 * @author Samuel Halliday
 * @deprecated in the hope that JUNG will fix these issues
 */
@Log
@Deprecated
public class FRLayoutFixed<V, E> extends FRLayout<V, E> {

    public FRLayoutFixed() {
        // https://sourceforge.net/tracker/?func=detail&aid=3542000&group_id=73840&atid=539119
        super(new UndirectedSparseGraph<V, E>());
    }

    @Override
    public void setSize(Dimension size) {
        // WORKAROUND: setSize doesn't touch the initialiser - ID: 3553281
        // https://sourceforge.net/tracker/?func=detail&aid=3553281&group_id=73840&atid=539119
        //
        // passing up will result in:
        //
        // FRLayout.setSize: an initialisation check (which will fail)
        // AbstractLayout.setSize: this.size = size (which is all we need to do the call for)
        // AbstractLayout.setSize: initialize()
        //                      -> doInit() and resetting of parameters
        // AbstractLayout.setSize: adjustLocations() (private, no @Override)
        //                      -> ConcurrentModificationException issues!
        // FRLayout.setSize: set max_dimension (private, no @Override)
        //
        // Instead, we manually set the 'size' here and reimplement expected logic

        Dimension oldSize = this.size;
        this.size = Preconditions.checkNotNull(size);
        Map<V, Point2D> adjustedLocations = Maps.newHashMap();
        for (V vertex : locations.keySet()) {
            if (!getGraph().getVertices().contains(vertex)) {
                // partially addressing "Layouts keeping Vertices alive - ID: 3553275"
                // http://sourceforge.net/tracker/?func=detail&aid=3553275&group_id=73840&atid=539119
                continue;
            }
            Point2D location = locations.get(vertex);
            if (location != null) {
                // and fix "setSize of GraphLayout move vertices to filling space - ID: 3553171"
                // https://sourceforge.net/tracker/index.php?func=detail&aid=3553171&group_id=73840&atid=539122
                adjustedLocations.put(vertex, rescaleLocation(location, oldSize, size));
            }
        }
        // setInitializer() will just reset the locations of everything, so we repeat the logic
        // also, why does AbstractLayout clone the vertex? Seems a pointless endeavour...
        RandomLocationTransformer<V> initialiser = new RandomLocationTransformer<V>(size);
        Map<V, Point2D> newLocations = LazyMap.decorate(adjustedLocations, initialiser);
        // synchronized is much better than looping/catching/ignoring ConcurrentModificationException
        this.locations = Collections.synchronizedMap(newLocations);

        try {
            // it seems a bit of a premature optimisation to ever calculate max_dimension,
            // since it is only ever used in done(). Here, we have to use reflection.
            Field max_dim_field = FRLayout.class.getDeclaredField("max_dimension");
            max_dim_field.setAccessible(true);
            double max = Math.max(size.width, size.height);
            max_dim_field.set(this, max);
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Reflection FAIL " + ex.getMessage(), ex);
        }
    }

    protected Point2D rescaleLocation(Point2D location, Dimension oldSize, Dimension newSize) {
        if (oldSize == null) {
            return location;
        }

        double x_percentage = location.getX() / oldSize.getWidth();
        double y_percentage = location.getY() / oldSize.getHeight();
        return new Point2D.Double(x_percentage * newSize.width, y_percentage * newSize.height);
    }
}
