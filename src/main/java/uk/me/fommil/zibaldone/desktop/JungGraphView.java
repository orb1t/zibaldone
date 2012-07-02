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
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.event.GraphEventListener;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JPanel;
import lombok.extern.java.Log;
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
    
    /**
     * @deprecated only to be used by GUI Editors.
     */
    @Deprecated
    public JungGraphView() {
        this.controller = null;
        this.graphVisualiser = null;
    }

    /**
     * @param controller 
     */
    public JungGraphView(JungMainController controller) {
        Preconditions.checkNotNull(controller);
        this.controller = controller;

        setLayout(new BorderLayout());

        ObservableGraph<Note, Weight> graph = controller.getGraph();
        final Layout<Note, Weight> graphLayout = new SpringLayout<Note, Weight>(graph, Weight.TRANSFORMER);
//        final Layout<Note, Weight> graphLayout = new FRLayout<Note, Weight>(graph);
//        final Layout<Note, Weight> graphLayout = new CircleLayout<Note, Weight>(graph);

        graphVisualiser = new VisualizationViewer<Note, Weight>(graphLayout);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent ce) {
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

    public Relaxer getRelaxer() {
        return graphVisualiser.getModel().getRelaxer();
    }
    
    @Override
    public void handleGraphEvent(GraphEvent<Note, Weight> evt) {
        //if (evt.getType().equals(GraphEvent.Type.))

        // TODO: very quickly check if the clusters have changed

        log.info("Not Implemented Yet");
//        throw new UnsupportedOperationException("Not supported yet.");
    }
}
