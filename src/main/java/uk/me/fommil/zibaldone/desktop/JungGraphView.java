/*
 * Created 24-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.base.Preconditions;
import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.event.GraphEventListener;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.util.Random;
import java.util.Set;
import javax.swing.JPanel;
import lombok.extern.java.Log;
import uk.me.fommil.utils.Convenience;
import uk.me.fommil.utils.Convenience.Loop;
import uk.me.fommil.zibaldone.Group;
import uk.me.fommil.zibaldone.Note;

/**
 * Draws the network graph of the {@link Note}s and {@link Group}s
 * using JUNG.
 * 
 * @see JungMainController
 * @author Samuel Halliday
 */
@Log
public class JungGraphView extends JPanel implements GraphEventListener<Note, Weight> {

    private final JungMainController controller;

    private final VisualizationViewer<Note, Weight> graphVisualiser;

    private final AggregateLayout<Note, Weight> graphLayout;

    /**
     * @deprecated only to be used by GUI Editors.
     */
    @Deprecated
    public JungGraphView() {
        this.controller = null;
        this.graphVisualiser = null;
        this.graphLayout = null;
    }

    /**
     * @param controller 
     */
    public JungGraphView(JungMainController controller) {
        Preconditions.checkNotNull(controller);
        this.controller = controller;

        setLayout(new BorderLayout());

        ObservableGraph<Note, Weight> graph = controller.getGraph();
//        Layout<Note, Weight> delegateLayout = new CircleLayout<Note, Weight>(graph);
        Layout<Note, Weight> delegateLayout = new SpringLayout<Note, Weight>(graph, Weight.TRANSFORMER);
        graphLayout = new AggregateLayout<Note, Weight>(delegateLayout);
        graphVisualiser = new VisualizationViewer<Note, Weight>(graphLayout);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent ce) {
                graphVisualiser.setSize(getSize());
                graphLayout.setSize(getSize());
            }
        });

        graphVisualiser.setBackground(Color.WHITE);
        add(graphVisualiser, BorderLayout.CENTER);

//        graph.addGraphEventListener(this);
    }

    public Relaxer getRelaxer() {
        return graphVisualiser.getModel().getRelaxer();
    }

    /**
     * The view responds to explicit changes in the clustering of notes.
     * 
     * @param clusters
     */
    public void setClusters(Set<Set<Note>> clusters) {
        // TODO: diff of what we have, to avoid needless redrawing
        graphLayout.removeAll();

        // TODO: create the subgraphs
        for (Set<Note> cluster : clusters) {
            Graph<Note, Weight> subGraph = buildSubgraph(cluster);
            Layout<Note, Weight> subLayout = new CircleLayout<Note, Weight>(subGraph);
            subLayout.setInitializer(graphLayout);
            subLayout.setSize(new Dimension(50,50));
            // TODO: calculate a good position/size for the cluster
            Random random = new Random();
            Point2D subCentered = new Point(random.nextInt(getSize().width), random.nextInt(getSize().height));
            graphLayout.put(subLayout, subCentered);
        }
    }

    private Graph<Note, Weight> buildSubgraph(Set<Note> cluster) {
        final Graph<Note, Weight> graph = controller.getGraph();
        final UndirectedSparseGraph<Note, Weight> subGraph = new UndirectedSparseGraph<Note, Weight>();
        for (Note note : cluster) {
            Preconditions.checkState(graph.containsVertex(note), note);
            subGraph.addVertex(note);
        }
        Convenience.upperOuter(cluster, new Loop<Note>() {
            @Override
            public void action(Note first, Note second) {
                Weight weight = graph.findEdge(first, second);
                if (weight != null) {
                    subGraph.addEdge(weight, first, second);
                }
            }
        });
        return subGraph;
    }

    @Override
    public void handleGraphEvent(GraphEvent<Note, Weight> evt) {
        log.info("Not Implemented Yet");
    }
}
