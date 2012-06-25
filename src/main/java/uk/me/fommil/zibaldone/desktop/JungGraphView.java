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
import uk.me.fommil.zibaldone.Cluster;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.desktop.JungMainController.Relation;

/**
 * Draws the network graph of the {@link Note}s and {@link Cluster}s
 * using JUNG.
 * 
 * @see JungMainController
 * @author Samuel Halliday
 */
public class JungGraphView extends JPanel implements GraphEventListener<Note, Relation> {

    private static final Logger log = Logger.getLogger(JungGraphView.class.getName());

    private static final long serialVersionUID = 1L;

    private final JungMainController controller;

    private static final Transformer<Relation, Integer> WEIGHTS = new Transformer<Relation, Integer>() {

        @Override
        public Integer transform(Relation input) {
            Preconditions.checkNotNull(input);
            return Math.round((float) (input.getWeight() * 1000.0));
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

        ObservableGraph<Note, Relation> graph = controller.getGraph();
        final Layout<Note, Relation> graphLayout = new SpringLayout<Note, Relation>(graph, WEIGHTS);
//        final Layout<Note, Relation> graphLayout = new FRLayout<Note, Relation>(graph);

//        final Layout<Note, Relation> graphLayout = new CircleLayout<Note, Relation>(graph);
        final VisualizationViewer<Note, Relation> graphVisualiser = new VisualizationViewer<Note, Relation>(graphLayout);
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
    public void handleGraphEvent(GraphEvent<Note, Relation> evt) {
        //if (evt.getType().equals(GraphEvent.Type.))

        log.info("Not Implemented Yet");
//        throw new UnsupportedOperationException("Not supported yet.");
    }
}
