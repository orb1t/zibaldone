/*
 * Created 24-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.GraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.layout.ObservableCachingLayout;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.picking.ShapePickSupport;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.font.TextAttribute;
import java.awt.geom.Point2D;
import java.text.AttributedString;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import lombok.Setter;
import lombok.extern.java.Log;
import org.apache.commons.collections15.Transformer;
import uk.me.fommil.jung.VertexIconShapeTransformerFixed;
import uk.me.fommil.jung.VisualizationViewerFixed;
import uk.me.fommil.jung.CircleLayoutFixed;
import uk.me.fommil.jung.AggregateLayoutFixed;
import uk.me.fommil.jung.FRLayoutFixed;
import uk.me.fommil.jung.BasicRendererFixed;
import uk.me.fommil.swing.SwingConvenience;
import uk.me.fommil.utils.Convenience;
import uk.me.fommil.zibaldone.Bunch;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.control.BunchController;
import uk.me.fommil.utils.JungGraphs;
import uk.me.fommil.utils.Lucene;
import uk.me.fommil.zibaldone.control.Listeners.BunchListener;
import uk.me.fommil.zibaldone.control.Listeners.ClusterId;
import uk.me.fommil.zibaldone.control.Listeners.ClusterListener;
import uk.me.fommil.zibaldone.control.TagController.TagChoice;
import uk.me.fommil.zibaldone.control.Weight;

/**
 * Draws the network graph of the {@link Note}s and {@link Bunch}s
 * using JUNG.
 * 
 * @author Samuel Halliday
 */
@Log
public class JungGraphView extends JPanel implements ClusterListener, BunchListener {

    private final VisualizationViewer<Note, Weight> graphVisualiser = new VisualizationViewerFixed<Note, Weight>();

    private final AggregateLayout<Note, Weight> graphLayout = new AggregateLayoutFixed<Note, Weight>();

    @Setter
    private BunchController bunchController;

    private final JPopupMenu popup = new JPopupMenu();

    // use the immutable BunchId as the key
    private Map<UUID, Layout<Note, Weight>> activeBunches = Maps.newHashMap();

    private final Map<ClusterId, Layout<Note, Weight>> clusters = Maps.newHashMap();

    public JungGraphView() {
        super(new BorderLayout());

        // TODO: set an initializer which uses a QuasiRandom sequence
        // TODO: check setSize (resize) support of the initialiser
        graphLayout.setDelegate(new FRLayoutFixed<Note, Weight>());

        graphVisualiser.setGraphLayout(graphLayout);
        graphVisualiser.setBackground(Color.WHITE);
        graphVisualiser.setDoubleBuffered(true);
        graphVisualiser.getRenderContext().setEdgeIncludePredicate(JungGraphs.<Note, Weight>noEdges());
        graphVisualiser.getRenderContext().setVertexIconTransformer(noteIcon);
        graphVisualiser.getRenderContext().setVertexShapeTransformer(new VertexIconShapeTransformerFixed<Note>(noteIcon));
        graphVisualiser.getRenderContext().setPickSupport(new ShapePickSupport<Note, Weight>(graphVisualiser));
        graphVisualiser.setRenderer(new BasicRendererFixed<Note, Weight>());
        graphVisualiser.setGraphMouse(new ModelessGraphMouse());

        add(graphVisualiser, BorderLayout.CENTER);
    }

    public void setGraph(ObservableGraph<Note, Weight> graph) {
        Preconditions.checkNotNull(graph);
        getGraphLayout().setGraph(graph);
    }

    private AggregateLayout<Note, Weight> getGraphLayout() {
        ObservableCachingLayout<Note, Weight> layout = (ObservableCachingLayout<Note, Weight>) graphVisualiser.getGraphLayout();
        return (AggregateLayout<Note, Weight>) layout.getDelegate();
    }

    private final Transformer<Note, Icon> noteIcon = new Transformer<Note, Icon>() {
        @Override
        public Icon transform(final Note note) {
            StringIcon icon = new StringIcon() {
                @Override
                public Color getBackground() {
                    // FIXME: this is a potential DB hit for every Note! Cache the colours.
                    Set<Bunch> bunches = belongsToActiveBunches(Collections.singleton(note));
                    boolean picked = graphVisualiser.getPickedVertexState().isPicked(note);
                    if (bunches.isEmpty()) {
                        if (picked) {
                            return new Color(255, 255, 0, 255);
                        } else {
                            return new Color(255, 255, 0, 128);
                        }
                    } else {
                        if (picked) {
                            return new Color(0, 255, 0, 255);
                        } else {
                            return new Color(0, 255, 0, 128);
                        }
                    }
                }
            };
            String shortened = Lucene.removeStopWords(note.getTitle());
            AttributedString text = new AttributedString(shortened);
            text.addAttribute(TextAttribute.FONT, new Font(Font.MONOSPACED, Font.PLAIN, 10));
            text.addAttribute(TextAttribute.FOREGROUND, Color.BLACK);

            icon.setMaximum(calculateNoteSize(5000, new Dimension(60, 40), 15000));
            icon.setText(text);
            return icon;
        }
    };

    // TODO: Graph changes Layout parameters, based on spaciousness, and caches NoteSize
    // lower, upper are thresholds and 'min' is the minimum dimension.
    private Dimension calculateNoteSize(float lower, Dimension min, float upper) {
        Dimension size = getSize();
        float spaciousness = (float) (size.getWidth() * size.getHeight()) / getGraphLayout().getGraph().getVertexCount();
        if (spaciousness < lower) {
            return min;
        } else if (spaciousness < upper) {
            float scaler = 1 + (spaciousness - 5000f) / (15000f - 5000f);
            assert scaler > 1 && scaler < 2 : scaler;
            return new Dimension(Math.round(min.width * scaler), Math.round(min.height * scaler));
        } else {
            return new Dimension(min.width * 2, min.height * 2);
        }
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
            super.mousePressed(e);
            location = e.getLocationOnScreen();
            pickedBefore = graphVisualiser.getPickedVertexState().getPicked().hashCode();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
            Set<Note> picked = graphVisualiser.getPickedVertexState().getPicked();

            if (!picked.isEmpty()) {
                // TODO: detect if dragged into a Bunch

                if (location.distance(e.getLocationOnScreen()) < 10
                        || picked.hashCode() != pickedBefore
                        || picked.size() > 1) {
                    selectNotes(picked);
                }
            }
        }
    }

    private void selectNotes(final Set<Note> notes) {
//        if (notes.size() == 1) {
//            showNote(Iterables.getOnlyElement(notes));
//            return;
//        }

        // TODO: reconsider the user interaction of what menus show up and when

        // TODO: move these notes to the front of the render context (focus them)



        popup.removeAll();

        Set<Bunch> memberOf = belongsToActiveBunches(notes);
        if (!memberOf.isEmpty()) {
            if (notes.size() == 1) {
                final Note note = Iterables.getOnlyElement(notes);
                JMenuItem item = new JMenuItem("Show \"" + note.getTitle() + "\"");
                item.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        showNote(note);
                    }
                });
                popup.add(new JSeparator());
            }
            for (final Bunch bunch : memberOf) {
                JMenuItem item = new JMenuItem("Show \"" + bunch.getName() + "\"");
                item.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        showBunch(bunch);
                    }
                });
                popup.add(item);
            }
            popup.add(new JSeparator());
            for (final Bunch bunch : memberOf) {
                JMenuItem item = new JMenuItem("Select all in \"" + bunch.getName() + "\"");
                item.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        PickedState<Note> picker = graphVisualiser.getPickedVertexState();
                        picker.clear();
                        for (Note note : bunch.getNotes()) {
                            picker.pick(note, true);
                        }
                    }
                });
                popup.add(item);
            }
            popup.add(new JSeparator());
        }

        popup.add(newBunchItem(notes));

        if (!activeBunches.isEmpty()) {
            popup.add(new JSeparator());
            popup.add(addToActiveBunchesItem(notes));
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

        SwingConvenience.popupAtMouse(popup, this);
    }

    private JMenuItem newBunchItem(final Set<Note> notes) {
        JMenuItem newBunchItem = new JMenuItem(BunchController.NEW_BUNCH_NAME, KeyEvent.VK_N);
        newBunchItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bunchController.newBunch(notes);
            }
        });
        return newBunchItem;
    }

    private JMenuItem addToActiveBunchesItem(final Set<Note> notes) {
        final JMenu menu = new JMenu("Add to Bunch");
        // lazy loading of bunches, a DB hit
        menu.addMenuListener(new MenuListener() {
            volatile boolean loaded;

            @Override
            public void menuSelected(MenuEvent e) {
                if (loaded) {
                    return;
                }
                loaded = true;
                Collection<Bunch> active = bunchController.getBunches(activeBunches.keySet());
                for (final Bunch bunch : active) {
                    JMenuItem addToBunch = new JMenuItem(bunch.getName());
                    addToBunch.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            bunch.getNotes().addAll(notes);
                            bunchController.updateBunch(bunch);
                        }
                    });
                    menu.add(addToBunch);
                }
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });

        return menu;
    }

    // TODO: should have add/view Bunch buttons OR drag & drop add to Bunch
    @Deprecated
    private void showNote(Note note) {
        NoteView noteView = new NoteView();
        noteView.setNote(note);
        SwingConvenience.showAsDialog("Note Viewer", noteView, true, null);
    }

    private void showBunch(Bunch bunch) {
        final BunchView view = new BunchView();
        view.setBunch(bunch);
        WindowListener listener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Bunch bunch = view.getBunch();
                bunchController.updateBunch(bunch);
            }
        };
        SwingConvenience.showAsDialog("Bunch Editor", view, true, listener);
    }

    // TODO: return the IDs only
    @Deprecated
    private Set<Bunch> belongsToActiveBunches(Set<Note> notes) {
        Set<Bunch> bunches = Sets.newHashSet();
        for (UUID bunchId : activeBunches.keySet()) {
            Set<Note> bunchNotes = Sets.newHashSet(activeBunches.get(bunchId).getGraph().getVertices());
            if (Convenience.isSubset(notes, bunchNotes)) {
                Bunch bunch = bunchController.getBunch(bunchId);
                bunches.add(bunch);
            }
        }
        return bunches;
    }

    @Override
    public void clusterAdded(ClusterId id, Set<Note> cluster) {
        Layout<Note, Weight> layout = createClump(cluster, false);
        clusters.put(id, layout);
    }

    @Override
    public void clusterRemoved(ClusterId id) {
        removeClump(clusters.get(id));
        clusters.remove(id);
    }

    @Override
    public void clusterUpdated(ClusterId id, Set<Note> cluster) {
        Layout<Note, Weight> subLayout = clusters.get(id);
        updateClump(subLayout, cluster);
    }

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
        UUID id = bunch.getId();
        switch (choice) {
            case SHOW:
                Preconditions.checkState(!activeBunches.containsKey(id));
                Layout<Note, Weight> layout = createClump(bunch.getNotes(), true);
                activeBunches.put(id, layout);
                if (BunchController.NEW_BUNCH_NAME.equals(bunch.getName())) {
                    showBunch(bunch);
                }
                break;
            case HIDE:
                Preconditions.checkState(activeBunches.containsKey(id));
                layout = activeBunches.get(id);
                removeClump(layout);
                activeBunches.remove(id);
        }
    }

    // a 'clump' is the super of both cluster and bunch
    private Layout<Note, Weight> createClump(Set<Note> notes, final boolean priority) {
        CircleLayoutFixed<Note, Weight> subLayout = new CircleLayoutFixed<Note, Weight>();
        subLayout.setPriority(priority);
        subLayout.setAggregate(getGraphLayout());

        Point2D position = calculateClumpPosition(notes, priority);
        getGraphLayout().put(subLayout, position);

        updateClump(subLayout, notes);

        return subLayout;
    }

    private Point2D calculateClumpPosition(Set<Note> notes, boolean priority) {
        if (!priority) {
            // TODO: quasirandom https://issues.apache.org/jira/browse/MATH-826
            Random random = new Random();
            double unitX = random.nextDouble();
            double unitY = random.nextDouble();
            Dimension size = getSize();
            Dimension padding = calculateClumpSize(notes);
            int x = (int) Math.min(Math.max(padding.width / 2, Math.round(size.width * unitX)), size.width - padding.width / 2);
            int y = (int) Math.min(Math.max(padding.height / 2, Math.round(size.height * unitY)), size.height - padding.height / 2);
            return new Point(x, y);
        }

        // TODO: could be smarter about where to put bunches (this fails when not new)
        Collection<Point2D> positions = JungGraphs.getPositions(getGraphLayout(), notes);
        return SwingConvenience.average(positions);
    }

    private Dimension calculateClumpSize(Set<Note> notes) {
        int size = Math.min(200, notes.size() * 10);
        return new Dimension(size, size);
    }

    private void removeClump(Layout<Note, Weight> layout) {
        AggregateLayout<Note, Weight> graphLayout = getGraphLayout();
        graphLayout.remove(layout);
        graphVisualiser.repaint();
    }

    private void updateClump(Layout<Note, Weight> layout, Set<Note> notes) {
        Graph<Note, Weight> graph = graphVisualiser.getGraphLayout().getGraph();
        Graph<Note, Weight> subGraph = JungGraphs.subGraph(graph, notes);

        layout.setGraph(subGraph);
        layout.setSize(calculateClumpSize(notes));
//        layout.setSize(new Dimension());

        graphVisualiser.repaint();
    }
}
