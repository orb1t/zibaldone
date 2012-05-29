/*
 * Created 24-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.base.Preconditions;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.event.GraphEventListener;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JPanel;
import org.apache.commons.collections15.Transformer;
import uk.me.fommil.zibaldone.Note;

/**
 * Draws the network graph of the {@link Note}s and {@link Cluster}s
 * using JUNG.
 * 
 * @see JungMainController
 * @author Samuel Halliday
 */
public class JungGraphView extends JPanel implements GraphEventListener<Note, Double> {

    private final ObservableGraph<Note, Double> graph;

    private final SpringLayout<Note, Double> graphLayout;

    private final BasicVisualizationServer<Note, Double> graphVisualiser;

    private final JungMainController controller;

    /**
     * For the benefit of the Netbeans UI designer.
     */
    public JungGraphView() {
        this(new ObservableGraph<Note, Double>(new SparseMultigraph<Note, Double>()));
    }

    /**
     * @param graph
     */
    public JungGraphView(ObservableGraph<Note, Double> graph) {
        this(graph, new JungMainController(graph));
    }

    /**
     * @param graph
     * @param controller 
     */
    public JungGraphView(ObservableGraph<Note, Double> graph, JungMainController controller) {
        Preconditions.checkNotNull(graph);
        this.graph = graph;
        this.controller = controller;

        Transformer<Double, Integer> weights = new Transformer<Double, Integer>() {

            @Override
            public Integer transform(Double input) {
                Preconditions.checkNotNull(input);
                Preconditions.checkArgument(input >= 0 && input <= 1, input);

                // TODO: might want to have a "clipped" view which can
                // remove edges after some threshold.

                return Math.round((float) (input * 1000.0));
            }
        };
        graphLayout = new SpringLayout<Note, Double>(graph, weights);
        graphVisualiser = new BasicVisualizationServer<Note, Double>(graphLayout);

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent ce) {
                graphLayout.setSize(getSize());
            }
        });

        add(graphVisualiser);

        graph.addGraphEventListener(this);
    }

    @Override
    public void handleGraphEvent(GraphEvent<Note, Double> evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ObservableGraph<Note, Double> getGraph() {
        return graph;
    }

    public JungMainController getController() {
        return controller;
    }
}
