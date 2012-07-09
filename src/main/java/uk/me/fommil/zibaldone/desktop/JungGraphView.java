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
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.event.GraphEventListener;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.ObservableCachingLayout;
import edu.uci.ics.jung.visualization.subLayout.GraphCollapser;
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
import lombok.Setter;
import lombok.extern.java.Log;
import uk.me.fommil.zibaldone.Group;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.desktop.JungMainController.ClustersChangedListener;

/**
 * Draws the network graph of the {@link Note}s and {@link Group}s
 * using JUNG.
 * 
 * @see JungMainController
 * @author Samuel Halliday
 */
@Log
public class JungGraphView extends JPanel implements GraphEventListener<Note, Weight>, ClustersChangedListener {

    private final VisualizationViewer<Note, Weight> graphVisualiser;
    
    @Setter
    private JungMainController controller;

    public JungGraphView() {
        super(new BorderLayout());
        // the JUNG API needs a Graph instance to instantiate many visual objects
        UndirectedSparseGraph<Note, Weight> dummy = new UndirectedSparseGraph<Note, Weight>();
        Layout<Note, Weight> delegateLayout = new SpringLayout<Note, Weight>(dummy, Weight.TRANSFORMER);
        Layout<Note, Weight> graphLayout = new AggregateLayout<Note, Weight>(delegateLayout);
        graphVisualiser = new VisualizationViewer<Note, Weight>(graphLayout);
        graphVisualiser.setBackground(Color.WHITE);

        // TODO: custom vertex icon, not painted circle
        graphVisualiser.getRenderContext().setVertexFillPaintTransformer(
                new PickableVertexPaintTransformer<Note>(
                graphVisualiser.getPickedVertexState(),
                Color.red, Color.yellow));        
        DefaultModalGraphMouse<Note, Weight> graphMouse = new DefaultModalGraphMouse<Note, Weight>();
        graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
        graphVisualiser.setGraphMouse(graphMouse);

        
        
        
        // TODO: popup Component, not tooltip
        graphVisualiser.setVertexToolTipTransformer(new ToStringLabeller<Note>());

        add(graphVisualiser, BorderLayout.CENTER);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent ce) {
                graphVisualiser.setSize(getSize());
                getGraphLayout().setSize(getSize());
            }
        });
    }

    /**
     * @param graph 
     */
    public void setGraph(ObservableGraph<Note, Weight> graph) {
        Preconditions.checkNotNull(graph);
        //        this.graph.removeGraphEventListener(this);
        getGraphLayout().setGraph(graph);
        //        graph.addGraphEventListener(this);
    }

    private AggregateLayout<Note, Weight> getGraphLayout() {
        ObservableCachingLayout<Note, Weight> layout = (ObservableCachingLayout<Note, Weight>) graphVisualiser.getGraphLayout();
        return (AggregateLayout<Note, Weight>) layout.getDelegate();
    }

    @SuppressWarnings("unchecked")
    private Graph<Note, Weight> subGraph(Set<Note> cluster) {
        Graph<Note, Weight> graph = graphVisualiser.getGraphLayout().getGraph();
        GraphCollapser collapser = new GraphCollapser(graph);
        return collapser.getClusterGraph(graph, cluster);
    }

    @Override
    public void handleGraphEvent(GraphEvent<Note, Weight> evt) {
        log.info("Not Implemented Yet");
    }

    @Override
    public void clustersChanged(Set<Set<Note>> clusters) {
        Preconditions.checkNotNull(clusters);
        
        AggregateLayout<Note, Weight> graphLayout = getGraphLayout();
        graphLayout.removeAll();
        for (Set<Note> cluster : clusters) {
            Graph<Note, Weight> subGraph = subGraph(cluster);
            Layout<Note, Weight> subLayout = new CircleLayout<Note, Weight>(subGraph);
            subLayout.setInitializer(graphLayout);
            subLayout.setSize(new Dimension(50, 50));
            // TODO: calculate a good position/size for the cluster
            Random random = new Random();
            Point2D subCentered = new Point(random.nextInt(getSize().width), random.nextInt(getSize().height));
            graphLayout.put(subLayout, subCentered);
        }
    }

    public void groupPicked() {
        Set<Note> picked = graphVisualiser.getPickedVertexState().getPicked();
        
//        controller.doGroup(picked);
        
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
