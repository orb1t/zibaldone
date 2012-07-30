/*
 * Created 29-Jul-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.jung;

import com.google.common.collect.Lists;
import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.Graph;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import uk.me.fommil.swing.SwingConvenience;
import uk.me.fommil.utils.JungGraphs;

/**
 * Overrides {@link CircleLayout} to fix a few bugs in JUNG.
 * 
 * @author Samuel Halliday
 * @deprecated in the hope that JUNG will fix these issues
 */
@Deprecated
public class CircleLayoutFixed<V, E> extends CircleLayout<V, E> {

    private final boolean priority;
    
    @Getter @Setter
    private AggregateLayout<V, E> aggregate;

    /**
     * @param graph
     * @param priority
     */
    public CircleLayoutFixed(Graph<V, E> graph, boolean priority) {
        super(graph);
        this.priority = priority;
    }

    @Override
    public int hashCode() {
        // https://sourceforge.net/tracker/?func=detail&aid=3550871&group_id=73840&atid=539119
        return priority ? 0 : 1;
    }

    @Override
    public void setSize(Dimension size) {
        if (size != null) {
            // https://sourceforge.net/tracker/?func=detail&aid=3551320&group_id=73840&atid=539119
            setRadius(0.45 * (size.height < size.width ? size.height : size.width));
        }
        super.setSize(size);
    }
    
    @Override
    public void setGraph(Graph<V, E> graph) {
        // https://sourceforge.net/tracker/?func=detail&aid=3551554&group_id=73840&atid=539119
        Graph<V, E> oldGraph = getGraph();
        if (oldGraph != null && !oldGraph.getVertices().isEmpty() && getAggregate() != null && getAggregate().getLayouts().containsKey(this)) {
            Collection<Point2D> positions = JungGraphs.getPositions(getAggregate(), oldGraph.getVertices());
            Point2D average = SwingConvenience.average(positions);
            getAggregate().put(this, average);
        }

        super.setGraph(graph);
        // https://sourceforge.net/tracker/?func=detail&aid=3551320&group_id=73840&atid=539119
        setVertexOrder(Lists.newArrayList(getGraph().getVertices()));
    }
}
