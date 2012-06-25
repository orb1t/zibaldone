/*
 * Created 28-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import edu.uci.ics.jung.graph.ObservableGraph;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import uk.me.fommil.zibaldone.Importer;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Reconciler;
import uk.me.fommil.zibaldone.Relator;
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
     * TODO: check this further... might not be true
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
        SynonymController synonymController = new SynonymController(emf);
        Relator relator = new TagRelator(synonymController.getActiveSynonyms());

        // a shame the API doesn't guarantee silent adding of vertices
        for (Note note1 : notes) {
            for (Note note2 : notes) {
                if (note1 == note2 || graph.getNeighbors(note1).contains(note2)) {
                    continue;
                }
                                
                Relation relation = new Relation(note1, note2);
                relation.setRelator(relator);
                // TODO: think about how sparsity is handled
                if (relation.getWeight() < 0.1) {
                    graph.addEdge(relation, note1, note2);
                }
            }
        }
        
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

    /**
     * @return the {@link Importer} implementations, indexed by their name.
     */
    public Map<String, Class<Importer>> getImporterImplementations() {
        ServiceLoader<Importer> importerService = ServiceLoader.load(Importer.class);
        HashMap<String, Class<Importer>> importerImpls = Maps.newHashMap();
        for (Importer importer : importerService) {
            String name = importer.getName();
            Class<Importer> klass = (Class<Importer>) importer.getClass();
            importerImpls.put(name, klass);
        }
        return importerImpls;
    }

    public Reconciler getReconciler() {
        return new Reconciler(emf);
    }

    public ObservableGraph<Note, Relation> getGraph() {
        return graph;
    }

    /**
     * Keeps all the settings in one place. Callers are encouraged to
     * edit mutable entries in-place but should immediately call one of
     * the controller's actions.
     *
     * @deprecated TODO: persist across sessions
     */
    @Deprecated
    public static class Settings implements Serializable {

        private boolean serendipity, tags = true, content, user;

        private double noise;

        private int seeds;

        private String search = "";

        private final List<String> includeTags = Lists.newArrayList();

        private final List<String> excludeTags = Lists.newArrayList();

        private final List<String> includeClusters = Lists.newArrayList();

        private final ListMultimap<Class<Importer>, Importer.Settings> importers = ArrayListMultimap.create();

        // <editor-fold defaultstate="collapsed" desc="BOILERPLATE GETTERS/SETTERS">
        public boolean isContent() {
            return content;
        }

        public void setContent(boolean content) {
            this.content = content;
        }

        public double getNoise() {
            return noise;
        }

        public void setNoise(double noise) {
            Preconditions.checkArgument(noise >= 0 && noise <= 1, noise);
            this.noise = noise;
        }

        public boolean isSerendipity() {
            return serendipity;
        }

        public int getSeeds() {
            return seeds;
        }

        public void setSeeds(int seeds) {
            Preconditions.checkArgument(seeds > 0);
            this.seeds = seeds;
        }

        public void setSerendipity(boolean serendipity) {
            this.serendipity = serendipity;
        }

        public boolean isTags() {
            return tags;
        }

        public void setTags(boolean tags) {
            this.tags = tags;
        }

        public boolean isUser() {
            return user;
        }

        public void setUser(boolean user) {
            this.user = user;
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

        public ListMultimap<Class<Importer>, Importer.Settings> getImporters() {
            return importers;
        }
        // </editor-fold>
    }

    public Settings getSettings() {
        return settings;
    }
}
