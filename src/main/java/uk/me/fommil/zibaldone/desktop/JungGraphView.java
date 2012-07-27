/*
 * Created 24-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import uk.me.fommil.zibaldone.control.GraphController;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.GraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.layout.ObservableCachingLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.swing.JDialog;
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
import uk.me.fommil.zibaldone.control.BunchController;
import uk.me.fommil.zibaldone.control.Listeners.BunchListener;
import uk.me.fommil.zibaldone.control.Listeners.ClusterId;
import uk.me.fommil.zibaldone.control.Listeners.ClusterListener;
import uk.me.fommil.zibaldone.control.TagController.TagChoice;
import uk.me.fommil.zibaldone.control.Weight;

/**
 * Draws the network graph of the {@link Note}s and {@link Bunch}s
 * using JUNG.
 * 
 * @see GraphController
 * @author Samuel Halliday
 */
@Log
public class JungGraphView extends JPanel implements ClusterListener, BunchListener {

    private final VisualizationViewer<Note, Weight> graphVisualiser;

    @Setter
    private BunchController bunchController;

    // TODO: make a JDialog which can be resized
    private final JPopupMenu popup = new JPopupMenu();

    // use the immutable BunchId as the key
    private Map<Long, Layout<Note, Weight>> activeBunches = Maps.newHashMap();

    // FIXME: ensure clusterLayouts do not override bunches
    private final Map<ClusterId, Layout<Note, Weight>> clusterLayouts = Maps.newHashMap();

    // TODO: a better way to store layout positions
    // double [0, 1]
    private final Map<Layout<Note, Weight>, Point2D.Double> positions = Maps.newHashMap();

    @Override
    public void bunchAdded(Bunch bunch) {
    }

    @Override
    public void bunchRemoved(Bunch bunch) {
        if (activeBunches.containsKey(bunch.getId())) {
            removeClump(activeBunches.get(bunch.getId()));
            activeBunches.remove(bunch.getId());
        }
    }

    @Override
    public void bunchUpdated(Bunch bunch) {
        Layout<Note, Weight> layout = activeBunches.get(bunch.getId());
        updateClump(layout, bunch.getNotes());
    }

    @Override
    public void bunchSelectionChanged(Bunch bunch, TagChoice choice) {
        switch (choice) {
            case SHOW:
                log.info("CLUMPING " + bunch);
                Preconditions.checkState(!activeBunches.containsKey(bunch.getId()));
                Layout<Note, Weight> layout = createClump(bunch.getNotes(), true);
                activeBunches.put(bunch.getId(), layout);
                break;
            case HIDE:
                Preconditions.checkState(activeBunches.containsKey(bunch.getId()));
                layout = activeBunches.get(bunch.getId());
                removeClump(layout);
                activeBunches.remove(bunch.getId());
        }
    }

    private void showBunch(Long bunchId) {
        Bunch bunch = bunchController.getBunch(bunchId);
        JDialog dialog = new JDialog();
        dialog.setTitle("Bunch Editor");
        dialog.setModal(true);
        final BunchView view = new BunchView();
        view.setBunch(bunch);
        dialog.add(view);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Bunch bunch = view.getBunch();
                bunchController.updateBunch(bunch);
            }
        });
        dialog.pack();
        dialog.setVisible(true);
    }

    private void showNote(Note note) {
        // TODO: Note JDialog not popup
        NoteView noteView = new NoteView();
        noteView.setNote(note);
        popup.add(noteView);
        popup();
    }

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

        private Point location;

        public ModelessGraphMouse() {
            add(picker);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            location = e.getLocationOnScreen();
            pickedBefore = graphVisualiser.getPickedVertexState().getPicked().hashCode();
            super.mousePressed(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
            Set<Note> picked = graphVisualiser.getPickedVertexState().getPicked();

            // TODO: adding an extra node should select, regardess of distance
            if (!picked.isEmpty() && location.distance(e.getLocationOnScreen()) < 10) {
                if (picked.hashCode() != pickedBefore || picked.size() > 1) {
                    selectNotes(picked);
                }
            }
        }
    }

    public JungGraphView() {
        super(new BorderLayout());
        // the JUNG API needs a Graph instance to instantiate many visual objects
        Graph<Note, Weight> dummy = new UndirectedSparseGraph<Note, Weight>();

        Layout<Note, Weight> delegateLayout = new FRLayout<Note, Weight>(dummy);
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

        add(graphVisualiser, BorderLayout.CENTER);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent ce) {
                graphVisualiser.setSize(getSize());
                getGraphLayout().setSize(getSize());

                for (Layout<Note, Weight> cluster : positions.keySet()) {
                    positionCluster(cluster);
                }
            }
        });
    }

    public void setGraph(ObservableGraph<Note, Weight> graph) {
        Preconditions.checkNotNull(graph);
        getGraphLayout().setGraph(graph);
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

    private void selectNotes(final Set<Note> notes) {
        assert notes != null;
        assert notes.size() > 0;

        popup.removeAll();

        // TODO: only go through active bunches
        for (Long bunchId : activeBunches.keySet()) {
            Set<Note> bunchNotes = Sets.newHashSet(activeBunches.get(bunchId).getGraph().getVertices());
            if (Convenience.isSubset(notes, bunchNotes)) {
                showBunch(bunchId);
                return;
            }
        }

        if (notes.size() == 1) {
            showNote(Iterables.getOnlyElement(notes));
            return;
        }

        JMenuItem newBunch = new JMenuItem("New Bunch", KeyEvent.VK_N);
        newBunch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bunchController.newBunch(notes);
            }
        });
        popup.add(newBunch);

        Collection<Bunch> bunches = bunchController.getBunches();
        if (!bunches.isEmpty() && bunches.size() < 10) {
            popup.add(new JSeparator());
            for (Bunch bunch : bunches) {
                popup.add(new JMenuItem("Add to \"" + bunch.getName() + "\""));
            }
        }

        if (notes.size() < 10) {
            popup.add(new JSeparator());
            for (Note note : notes) {
                JMenuItem entry = new JMenuItem(note.getTitle());
                entry.setEnabled(false);
                entry.setFont(entry.getFont().deriveFont(10f));
                popup.add(entry);
            }
        }
        popup();
    }

    private void popup() {
        popup.pack();
        Point mouse = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(mouse, this);
        // http://stackoverflow.com/questions/766956
        popup.show(this, mouse.x, mouse.y);
    }

    // resets the location of the cluster by scaling 'clusterPositions' to the
    // window size, also with some padding on the border.
    private void positionCluster(Layout<Note, Weight> layout) {
        AggregateLayout<Note, Weight> graphLayout = getGraphLayout();
        Point2D position = positions.get(layout);
        Dimension size = getSize();
        Dimension padding = layout.getSize();
        int x = (int) min(max(padding.width / 2, round(size.width * position.getX())), size.width - padding.width / 2);
        int y = (int) min(max(padding.height / 2, round(size.height * position.getY())), size.height - padding.height / 2);
        Point location = new Point(x, y);
        graphLayout.put(layout, location);
    }

    private Dimension calculateClusterSize(Set<Note> cluster) {
        int size = Math.min(75, cluster.size() * 10);
        return new Dimension(size, size);
    }

    @Override
    public void clusterAdded(ClusterId id, Set<Note> cluster) {
        Preconditions.checkNotNull(id);

        Layout<Note, Weight> layout = createClump(cluster, false);
        clusterLayouts.put(id, layout);
    }

    @Override
    public void clusterRemoved(ClusterId id) {
        Preconditions.checkNotNull(id);
        Preconditions.checkArgument(clusterLayouts.containsKey(id));

        removeClump(clusterLayouts.get(id));
        clusterLayouts.remove(id);
    }

    @Override
    public void clusterUpdated(ClusterId id, Set<Note> cluster) {
        Preconditions.checkNotNull(id);
        Preconditions.checkArgument(clusterLayouts.containsKey(id));

        Layout<Note, Weight> subLayout = clusterLayouts.get(id);
        updateClump(subLayout, cluster);
    }

    private Layout<Note, Weight> createClump(Set<Note> notes, final boolean priority) {
        Graph<Note, Weight> subGraph = subGraph(notes);
        
        Layout<Note, Weight> subLayout = new CircleLayout<Note, Weight>(subGraph) {

            // HACK: forces ordering when drawing layout
            @Override
            public int hashCode() {
                return priority ? 0 : 1;
            }
            
        };
        subLayout.setInitializer(getGraphLayout());
        subLayout.setSize(calculateClusterSize(notes));

        // TODO: smarter positioning of clumps
        // https://issues.apache.org/jira/browse/MATH-826
        Random random = new Random();
        Point2D.Double position = new Point2D.Double();
        position.setLocation(random.nextDouble(), random.nextDouble());

        positions.put(subLayout, position);
        positionCluster(subLayout);
        graphVisualiser.repaint();

        return subLayout;
    }

    private void removeClump(Layout<Note, Weight> layout) {
        AggregateLayout<Note, Weight> graphLayout = getGraphLayout();
        graphLayout.remove(layout);
        positions.remove(layout);
        graphVisualiser.repaint();
    }

    private void updateClump(Layout<Note, Weight> layout, Set<Note> notes) {
        layout.setSize(calculateClusterSize(notes));
        Graph<Note, Weight> subGraph = subGraph(notes);
        layout.setGraph(subGraph);
        graphVisualiser.repaint();
    }
}
