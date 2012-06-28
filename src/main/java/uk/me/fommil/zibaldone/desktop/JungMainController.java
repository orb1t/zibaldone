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
import edu.uci.ics.jung.graph.ObservableGraph;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
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
public class JungMainController {

    /**
     * FIXME: this is not true, but is it useful to keep this object?
     * 
     * JUNG enforces unique objects on edges, so it is not possible to
     * use just the weights as edges: so we use this.
     */
    public static class Relation {

        private final Note noteA, noteB;

        /**
         * @param a
         * @param b
         */
        public Relation(Note a, Note b) {
            this.noteA = a;
            this.noteB = b;
        }

        private Relator relator;

        public double getWeight() {
            return relator.relate(noteA, noteB);
        }

        // <editor-fold defaultstate="collapsed" desc="BOILERPLATE GETTERS/SETTERS">
        public Relator getRelator() {
            return relator;
        }

        public void setRelator(Relator relator) {
            this.relator = relator;
        }

        public Note getNoteA() {
            return noteA;
        }

        public Note getNoteB() {
            return noteB;
        }
        // </editor-fold>        
    }

    private static final Logger log = Logger.getLogger(JungMainController.class.getName());

    private final EntityManagerFactory emf;

    private final ObservableGraph<Note, Relation> graph;

    private final Settings settings = new Settings();

    /**
     * @param emf
     * @param graph the model
     */
    public JungMainController(EntityManagerFactory emf, ObservableGraph<Note, Relation> graph) {
        Preconditions.checkNotNull(emf);
        Preconditions.checkNotNull(graph);
        this.emf = emf;
        this.graph = graph;
    }

    /**
     * Updates the model based on current settings.
     */
    public void doRefresh() {
        // TODO: update/delete/add instead of bruteforce clear/create

        Collection<Note> vertices = graph.getVertices();
        for (Note note : vertices) {
            graph.removeVertex(note);
        }

        NoteDao noteDao = new NoteDao(emf);
        List<Note> notes = noteDao.readAll();
        for (Note note : notes) {
            graph.addVertex(note);
        }

        // TODO: choose relevant relator
        final Relator relator = new TagRelator();
        relator.refresh(emf);

        // update the graph object
        Convenience.upperOuter(notes, new Loop<Note>() {

            @Override
            public void action(Note first, Note second) {
                Relation relation = new Relation(first, second);
                relation.setRelator(relator);
                double weight = relation.getWeight();
                if (weight < 1) {
//                    log.info(weight + " between " + first.getTags() + " | " + second.getTags());
                    graph.addEdge(relation, first, second);
                }
            }
        });
        // now tell the view how to visually cluster the notes
        // like in the SubLayoutDemo (AggregateLayout)
        Set<Set<Note>> clusters = relator.cluster(vertices);

        // TODO: tell the view

        log.info(graph.getVertexCount() + " vertices, " + graph.getEdgeCount() + " edges");
    }

    /**
     * Automatically create a cluster, as a starting point.
     */
    public void doAutoCluster() {
        log.info("Not Implemented Yet");
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

    public ObservableGraph<Note, Relation> getGraph() {
        return graph;
    }

    /**
     * Keeps all the persistent settings in one place.
     *
     * @deprecated TODO: API review to persist across sessions
     */
    @Deprecated
    public static class Settings implements Serializable {

        private int seeds;

        private String search = "";

        private final List<String> includeTags = Lists.newArrayList();

        private final List<String> excludeTags = Lists.newArrayList();

        private final List<String> includeClusters = Lists.newArrayList();

        private final Map<UUID, Importer> importers = Maps.newHashMap();

        private final List<Relator> relators = Lists.newArrayList();

        // <editor-fold defaultstate="collapsed" desc="BOILERPLATE GETTERS/SETTERS">
        public int getSeeds() {
            return seeds;
        }

        public void setSeeds(int seeds) {
            Preconditions.checkArgument(seeds > 0);
            this.seeds = seeds;
        }

        public void setSearch(String search) {
            Preconditions.checkNotNull(search);
            this.search = search;
        }

        public String getSearch() {
            return search;
        }

        public List<String> getExcludeTags() {
            return excludeTags;
        }

        public List<String> getIncludeTags() {
            return includeTags;
        }

        public List<String> getIncludeClusters() {
            return includeClusters;
        }

        public Map<UUID, Importer> getImporters() {
            return importers;
        }

        public List<Relator> getRelators() {
            return relators;
        }
        // </editor-fold>
    }

    public Settings getSettings() {
        return settings;
    }
}
