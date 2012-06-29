/*
 * Created 24-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.base.Preconditions;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.event.GraphEventListener;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.apache.commons.collections15.Transformer;
import uk.me.fommil.zibaldone.Group;
import uk.me.fommil.zibaldone.Note;

/**
 * Draws the network graph of the {@link Note}s and {@link Group}s
 * using JUNG.
 * 
 * @see JungMainController
 * @author Samuel Halliday
 */
public class JungGraphView extends JPanel implements GraphEventListener<Note, Double> {

    private static final Logger log = Logger.getLogger(JungGraphView.class.getName());

    private static final long serialVersionUID = 1L;

    private final JungMainController controller;

    private static final Transformer<Double, Integer> WEIGHTS = new Transformer<Double, Integer>() {

        @Override
        public Integer transform(Double input) {
            Preconditions.checkNotNull(input);
            return Math.round((float) (input * 1000.0));
        }
    };

    /**
     * @deprecated only to be used by GUI Editors.
     */
    @Deprecated
    public JungGraphView() {
        this.controller = null;
    }

    /**
     * @param controller 
     */
    public JungGraphView(JungMainController controller) {
        Preconditions.checkNotNull(controller);
        this.controller = controller;

        setLayout(new BorderLayout());

        ObservableGraph<Note, Double> graph = controller.getGraph();
        final Layout<Note, Double> graphLayout = new SpringLayout<Note, Double>(graph, WEIGHTS);
//        final Layout<Note, Double> graphLayout = new FRLayout<Note, Double>(graph);

//        final Layout<Note, Double> graphLayout = new CircleLayout<Note, Double>(graph);
        final VisualizationViewer<Note, Double> graphVisualiser = new VisualizationViewer<Note, Double>(graphLayout);
//        final GraphZoomScrollPane zoom = new GraphZoomScrollPane(graphVisualiser);

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent ce) {
//                zoom.setSize(getSize());
                graphVisualiser.setSize(getSize());
                graphLayout.setSize(getSize());
            }
        });

        graphVisualiser.setBackground(Color.WHITE);
//        add(zoom, BorderLayout.CENTER);
        add(graphVisualiser, BorderLayout.CENTER);

//        graphVisualiser.getModel().getRelaxer().setSleepTime(100);

//        graph.addGraphEventListener(this);
    }

    @Override
    public void handleGraphEvent(GraphEvent<Note, Double> evt) {
        //if (evt.getType().equals(GraphEvent.Type.))

        // TODO: very quickly check if the clusters have changed
        
        log.info("Not Implemented Yet");
//        throw new UnsupportedOperationException("Not supported yet.");
    }
}
