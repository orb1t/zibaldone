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
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.GraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.ObservableCachingLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Random;
import java.util.Set;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import lombok.Setter;
import lombok.extern.java.Log;
import uk.me.fommil.utils.Convenience;
import uk.me.fommil.utils.Convenience.Loop;
import uk.me.fommil.zibaldone.Bunch;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.desktop.JungMainController.ClustersChangedListener;

/**
 * Draws the network graph of the {@link Note}s and {@link Bunch}s
 * using JUNG.
 * 
 * @see JungMainController
 * @author Samuel Halliday
 */
@Log
public class JungGraphView extends JPanel implements ClustersChangedListener {

    private final VisualizationViewer<Note, Weight> graphVisualiser;

    @Setter
    private JungMainController controller;

    private final JPopupMenu contextMenu = new JPopupMenu();

    /**
     * The JUNG mouse handling framework was developed
     * to allow different plugins to function under different user-selected
     * "modes", which is detrimental to the Zibaldone user experience.
     * <p>
     * Here we go back to the highest abstraction in the JUNG mouse handling
     * logic and provide a lightweight "modeless" experience that should be
     * feasible to extend to provide multi-touch support.
     * <p>
     * http://stackoverflow.com/questions/369301
     * TODO: touchpad swipe gestures = scrolling up/down/left/right
     * TODO: touchpad pinching = zoom
     */
    private class ModelessGraphMouse extends PluggableGraphMouse {

        private final GraphMousePlugin picker = new PickingGraphMousePlugin<Note, Weight>();

        private int pickedBefore;

        public ModelessGraphMouse() {
            add(picker);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            pickedBefore = graphVisualiser.getPickedVertexState().getPicked().size();
            super.mousePressed(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
            Set<Note> picked = graphVisualiser.getPickedVertexState().getPicked();

            if (picked.size() > 1 && picked.size() != pickedBefore) {
                selectNotes(picked);
            }
        }
    }

    public JungGraphView() {
        super(new BorderLayout());
        // the JUNG API needs a Graph instance to instantiate many visual objects
        Graph<Note, Weight> dummy = new UndirectedSparseGraph<Note, Weight>();
        Layout<Note, Weight> delegateLayout = new SpringLayout<Note, Weight>(dummy, Weight.TRANSFORMER);
        Layout<Note, Weight> graphLayout = new AggregateLayout<Note, Weight>(delegateLayout);
        graphVisualiser = new VisualizationViewer<Note, Weight>(graphLayout);
        graphVisualiser.setBackground(Color.WHITE);

        // TODO: don't draw edges (removes problem of edge selection)

        // TODO: custom vertex icon, not painted circle
        graphVisualiser.getRenderContext().setVertexFillPaintTransformer(
                new PickableVertexPaintTransformer<Note>(
                graphVisualiser.getPickedVertexState(),
                Color.red, Color.yellow));

        graphVisualiser.setGraphMouse(new ModelessGraphMouse());

        // TODO: popup Note/Bunch Component, not tooltip
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

    private Graph<Note, Weight> subGraph(Set<Note> cluster) {
        // the GraphCollapser supposedly provides this functionality, but is unreliable
        // Graph<Note, Weight> graph = graphVisualiser.getGraphLayout().getGraph();
        // GraphCollapser collapser = new GraphCollapser(graph);
        // return collapser.getClusterGraph(graph, cluster);
        final Graph<Note, Weight> graph = graphVisualiser.getGraphLayout().getGraph();
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

    private void selectNotes(final Collection<Note> notes) {
        assert notes != null;
        assert notes.size() > 0;

        contextMenu.removeAll();

        JMenuItem newBunch = new JMenuItem("New Bunch", KeyEvent.VK_N);
        newBunch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.newBunch(notes);
            }
        });
        contextMenu.add(newBunch);

        Collection<Bunch> bunches = controller.getBunches();
        if (!bunches.isEmpty() && bunches.size() < 10) {
            contextMenu.add(new JSeparator());
            for (Bunch bunch : bunches) {
                contextMenu.add(new JMenuItem("Add to \"" + bunch.getName() + "\""));
            }
        }

        if (notes.size() < 10) {
            contextMenu.add(new JSeparator());
            for (Note note : notes) {
                JMenuItem entry = new JMenuItem(note.getTitle());
                entry.setEnabled(false);
                Font font = entry.getFont();
                entry.setFont(font.deriveFont(0.8f * font.getSize()));
                contextMenu.add(entry);
            }
        }
        contextMenu.pack();

        Point mouse = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(mouse, this);
        // http://stackoverflow.com/questions/766956
        contextMenu.show(this, mouse.x, mouse.y);
    }

    @Override
    public void clustersChanged(Set<Set<Note>> clusters) {
        Preconditions.checkNotNull(clusters);

        // TODO: keep a record of the clusters so they don't jump around
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
}
