/*
 * Created 01-Aug-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.jung;

import com.google.common.collect.Sets;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.renderers.BasicRenderer;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Set;

/**
 * Overrides {@link BasicRenderer} to fix a few bugs in JUNG.
 * 
 * @author Samuel Halliday
 * @deprecated in the hope that JUNG will fix these issues
 */
@Deprecated
public class BasicRendererFixed<V, E> extends BasicRenderer<V, E> {

    @Override
    public void render(RenderContext<V, E> renderContext, Layout<V, E> layout) {
        try {
            Set<E> edges = Sets.newHashSet(layout.getGraph().getEdges());
            Set<E> pickedEdges = renderContext.getPickedEdgeState().getPicked();
            paintEdges(renderContext, layout, Sets.difference(edges, pickedEdges));
            paintEdges(renderContext, layout, pickedEdges);

            Set<V> vertices = Sets.newHashSet(layout.getGraph().getVertices());
            Set<V> pickedVertices = renderContext.getPickedVertexState().getPicked();
            paintVertices(renderContext, layout, Sets.difference(vertices, pickedVertices));
            paintVertices(renderContext, layout, pickedVertices);
        } catch (ConcurrentModificationException cme) {
            renderContext.getScreenDevice().repaint();
        }
    }

    private void paintEdges(RenderContext<V, E> renderContext, Layout<V, E> layout, Collection<E> edges) {
        for (E e : edges) {
            renderEdge(renderContext, layout, e);
            renderEdgeLabel(renderContext, layout, e);
        }
    }

    private void paintVertices(RenderContext<V, E> renderContext, Layout<V, E> layout, Collection<V> vertices) {
        for (V v : vertices) {
            renderVertex(renderContext, layout, v);
            renderVertexLabel(renderContext, layout, v);
        }
    }
}
