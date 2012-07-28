/*
 * Created 28-Jul-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.control;

import com.google.common.base.Preconditions;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.GraphDecorator;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import java.util.Collection;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import uk.me.fommil.utils.Convenience;

/**
 * Utility methods for working with JUNG {@link Graph} objects.
 *
 * @author Samuel Halliday
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JungGraphs {

    /**
     * Construct a sub-graph of the input, restricting to some vertices.
     * <p>
     * Only undirected graphs currently supported.
     * 
     * @param <V>
     * @param <E>
     * @param graph
     * @param vertices those not found in graph will be ignored
     * @return
     */
    public static <V, E> Graph<V, E> subGraph(final Graph<V, E> graph, Set<V> vertices) {
        // the GraphCollapser supposedly provides this functionality, but is unreliable
        // GraphCollapser collapser = new GraphCollapser(graph);
        // return collapser.getClusterGraph(graph, cluster);
        Preconditions.checkNotNull(graph);
        Preconditions.checkNotNull(vertices);
        Preconditions.checkArgument(isUndirected(graph));

        final Graph<V, E> subGraph = new UndirectedSparseGraph<V, E>();
        for (V v : vertices) {
            if (graph.containsVertex(v)) {
                subGraph.addVertex(v);
            }
        }
        Convenience.upperOuter(subGraph.getVertices(), new Convenience.Loop<V>() {
            @Override
            public void action(V first, V second) {
                E edge = graph.findEdge(first, second);
                if (edge != null) {
                    subGraph.addEdge(edge, first, second);
                }
            }
        });
        return subGraph;
    }

    /**
     * Converts the first parameter to match the second, attempting to minimise
     * the number of changes. Very useful for changing {@link ObservableGraph}
     * instances.
     * <p>
     * Only undirected graphs currently supported.
     *
     * @param <V>
     * @param <E>
     * @param graph
     * @param update
     */
    public static <V, E> void updateGraph(final Graph<V, E> graph, final Graph<V, E> update) {
        Preconditions.checkNotNull(graph);
        Preconditions.checkNotNull(update);
        Preconditions.checkArgument(isUndirected(graph));
        Preconditions.checkArgument(isUndirected(update));

        for (V note : graph.getVertices()) {
            if (!update.getVertices().contains(note)) {
                graph.removeVertex(note);
            }
        }
        for (V note : update.getVertices()) {
            if (!graph.getVertices().contains(note)) {
                graph.addVertex(note);
            }
        }
        Convenience.upperOuter(update.getVertices(), new Convenience.Loop<V>() {
            @Override
            public void action(V first, V second) {
                E existing = graph.findEdge(first, second);
                E replace = update.findEdge(first, second);
                if (replace == null && existing != null) {
                    graph.removeEdge(existing);
                }
                if (replace != null && !replace.equals(existing)) {
                    graph.addEdge(replace, first, second);
                }
            }
        });
    }

    /**
     * @param <V>
     * @param <E>
     * @param graph
     * @return true if the given graph doesn't have any directed edges.
     */
    public static <V, E> boolean isUndirected(Graph<V, E> graph) {
        Preconditions.checkNotNull(graph);
        if (graph instanceof UndirectedGraph) {
            return true;
        }
        if (graph instanceof GraphDecorator) {
            GraphDecorator<V, E> decorator = (GraphDecorator<V, E>) graph;
            Collection<E> directedEdges = decorator.getEdges(EdgeType.DIRECTED);
            if (directedEdges == null || directedEdges.isEmpty()) {
                // https://sourceforge.net/tracker/?func=detail&aid=3550870&group_id=73840&atid=539119
                return true;
            }
        }
        return false;
    }
}
