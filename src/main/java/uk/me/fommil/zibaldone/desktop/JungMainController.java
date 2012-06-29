/*
 * Created 28-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import java.io.Serializable;
import java.util.*;
import javax.persistence.EntityManagerFactory;
import lombok.Data;
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
public class JungMainController {

    private final EntityManagerFactory emf;

    private final ObservableGraph<Note, Double> graph;

    private final Set<Set<Note>> clusters = Sets.newHashSet();

    private final Settings settings = new Settings();

    /**
     * @param emf
     * @param graph the model
     */
    public JungMainController(EntityManagerFactory emf, ObservableGraph<Note, Double> graph) {
        Preconditions.checkNotNull(emf);
        Preconditions.checkNotNull(graph);
        this.emf = emf;
        this.graph = graph;
    }

    /**
     * Updates the model based on current settings.
     */
    public void doRefresh() {
        final Graph<Note, Double> update = new UndirectedSparseGraph<Note, Double>();
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
                if (weight < 1) {
//                    log.info(weight + " between " + first.getTags() + " | " + second.getTags());
                    update.addEdge(weight, first, second);
                }
            }
        });

        clusters.clear();
        for (Set<Note> cluster : relator.cluster(notes)) {
            clusters.add(Collections.unmodifiableSet(cluster));
        }

        // TODO: visually cluster the selected Groups

        log.info(update.getVertexCount() + " vertices, " + update.getEdgeCount() + " edges");
    }

    // updates the 'graph' object, with minimal changes, to match the parameter
    private void update(UndirectedSparseGraph<Note, Double> update) {
        Collection<Note> vertices = graph.getVertices();
        for (Note note : vertices) {
            graph.removeVertex(note);
        }
        for (Note note : update.getVertices()) {
            graph.addVertex(note);
        }
        for (Note note : update.getVertices()) {
            // find...
            // graph.get
        }
        
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

    public Set<Set<Note>> getClusters() {
        return Collections.unmodifiableSet(clusters);
    }

    public ObservableGraph<Note, Double> getGraph() {
        return graph;
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

    public Settings getSettings() {
        return settings;
    }
}
