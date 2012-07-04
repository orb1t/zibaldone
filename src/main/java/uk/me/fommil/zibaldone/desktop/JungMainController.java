/*
 * Created 28-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import java.io.Serializable;
import java.util.*;
import javax.persistence.EntityManagerFactory;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import uk.me.fommil.utils.Convenience;
import uk.me.fommil.utils.Convenience.Loop;
import uk.me.fommil.zibaldone.*;
import uk.me.fommil.zibaldone.persistence.NoteDao;
import uk.me.fommil.zibaldone.relator.TagRelator;

/**
 * An MVC Controller that uses JUNG to organise the core
 * Zibaldone objects (the Model). Most tasks
 * are performed by specialist controllers, obtained here.
 * <p>
 * The View primarily receives asynchronous updates via {@link ObservableGraph}.
 * However, some information is available synchronously through getters/setters.
 * Standard Java objects are preferred in the Controller APIs to simplify
 * the Views and to minimise the risk of incorrect persistence handling.
 * <p>
 * The JUNG graph has {@link Note}s on vertices and {@link Double} values
 * {@code [0, 1]}, as defined in the {@link Relator}, on the edges.
 * 
 * @see JungGraphView
 * @author Samuel Halliday
 */
@Log
@RequiredArgsConstructor
public class JungMainController {

    @NonNull
    private final EntityManagerFactory emf;

    @Getter @NonNull
    private final ObservableGraph<Note, Weight> graph;

    @Getter
    private final Set<Set<Note>> clusters = Sets.newHashSet();

    @Getter
    private final Settings settings = new Settings();

    @Setter
    private JungGraphView view;

    /**
     * Updates the model based on current settings.
     */
    public void doRefresh() {
        final UndirectedSparseGraph<Note, Weight> update = new UndirectedSparseGraph<Note, Weight>();
        NoteDao noteDao = new NoteDao(emf);
        List<Note> notes = noteDao.readAll();
        for (Note note : notes) {
            update.addVertex(note);
        }

        // TODO: choose relevant relator
        final Relator relator = new TagRelator();
        relator.refresh(emf);

        Convenience.upperOuter(notes, new Loop<Note>() {
            @Override
            public void action(Note first, Note second) {
                double weight = relator.relate(first, second);
                // TODO: parameterise this number
                if (weight < 0.8) {
//                    log.info(weight + " between " + first.getTags() + " | " + second.getTags());
                    update.addEdge(new Weight(weight), first, second);
                }
            }
        });

        clusters.clear();
        for (Set<Note> cluster : relator.cluster(notes)) {
            log.info("Cluster size: " + cluster.size());
            if (cluster.size() > 0) {
                clusters.add(cluster);
            }
        }

        log.info(update.getVertexCount() + " vertices, " + update.getEdgeCount() + " edges, " + clusters.size() + " clusters");

        // TODO: investigate how the Relaxer works
//        Relaxer relaxer = view.getRelaxer();
//        relaxer.pause();
        update(update);
        view.setClusters(clusters);

        // TODO: visually cluster the selected Groups

//        relaxer.resume();
    }

    // updates the 'graph' object, with minimal changes, to match the parameter
    private void update(final UndirectedSparseGraph<Note, Weight> update) {
        Set<Note> newVertices = Sets.newHashSet(update.getVertices());
        {
            Set<Note> oldVertices = Sets.newHashSet(graph.getVertices());
            // updates to fields of Note will result in them being removed and replaced entirely
            for (Note note : oldVertices) {
                if (!newVertices.contains(note)) {
                    graph.removeVertex(note);
                }
            }
            for (Note note : newVertices) {
                if (!oldVertices.contains(note)) {
                    graph.addVertex(note);
                }
            }
        }

        Convenience.upperOuter(newVertices, new Loop<Note>() {
            @Override
            public void action(Note first, Note second) {
                Weight oldWeight = graph.findEdge(first, second);
                Weight newWeight = update.findEdge(first, second);
                if (oldWeight == null && newWeight != null) {
                    graph.addEdge(newWeight, first, second);
                }
                if (oldWeight != null && newWeight == null) {
                    graph.removeEdge(oldWeight);
                }
                if (oldWeight != null && newWeight != null) {
                    if (oldWeight.getWeight() != newWeight.getWeight()) {
                        graph.removeEdge(oldWeight);
                        graph.addEdge(newWeight, first, second);
                    }
                }
            }
        });
    }

    /**
     * @return all tags with their usage counts
     */
    public Map<String, Integer> getTags() {
        log.info("Not Implemented Yet");
        return Maps.newHashMap();
    }

    public Reconciler getReconciler() {
        return new Reconciler(emf);
    }

    /**
     * Keeps all the persistent settings in one place.
     *
     * @deprecated TODO: API review to persist across sessions
     */
    @Deprecated
    @Data
    public static class Settings implements Serializable {

        private int seeds;

        private String search = "";

        private final List<String> showWithTags = Lists.newArrayList();

        private final List<String> hideWithTags = Lists.newArrayList();

        private final List<String> showGroups = Lists.newArrayList();

        private final Map<UUID, Importer> importers = Maps.newHashMap();

        private final List<Relator> relators = Lists.newArrayList();

    }
}
