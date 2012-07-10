/*
 * Created 28-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import java.util.*;
import javax.annotation.Nullable;
import javax.persistence.EntityManagerFactory;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.ListenerSupport;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import uk.me.fommil.utils.Convenience;
import uk.me.fommil.utils.Convenience.Loop;
import uk.me.fommil.zibaldone.*;
import uk.me.fommil.zibaldone.desktop.JungMainController.ClustersChangedListener;
import uk.me.fommil.zibaldone.desktop.JungMainController.BunchesChangedListener;
import uk.me.fommil.zibaldone.desktop.JungMainController.TagsChangedListener;
import uk.me.fommil.zibaldone.persistence.BunchDao;
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
 * The JUNG graph has {@link Note}s on vertices and {@link Weight} values
 * {@code [0, 1]}, as defined in the {@link Relator}, on the edges.
 * 
 * @see JungGraphView
 * @author Samuel Halliday
 */
@Log
@RequiredArgsConstructor
@ListenerSupport({BunchesChangedListener.class, ClustersChangedListener.class, TagsChangedListener.class})
public class JungMainController {

    public interface BunchesChangedListener extends EventListener {

        // TODO: fire the change, not the object
        public void bunchesChanged(Collection<Bunch> bunches);

        // TODO: fire the change, not the object
        public void selectedBunchesChanged(Collection<Bunch> bunches);
    }

    public interface ClustersChangedListener extends EventListener {

        // TODO: fire the change, not the object
        public void clustersChanged(Set<Set<Note>> clusters);
    }

    public interface TagsChangedListener extends EventListener {

        // TODO: fire the change, not the object
        public void tagSelectionChanged(Multimap<TagChoice, Tag> selection);

        // TODO: fire the change, not the object
        public void tagsChanged(Set<Tag> tags);
    }

    @NonNull @Getter(AccessLevel.PROTECTED)
    private final EntityManagerFactory emf;

    @Getter @NonNull
    private final ObservableGraph<Note, Weight> graph;

    @Getter
    private final Settings settings = new Settings();

    /**
     * Updates the model based on current settings.
     */
    public void doRefresh() {
        final UndirectedSparseGraph<Note, Weight> update = new UndirectedSparseGraph<Note, Weight>();
        NoteDao noteDao = new NoteDao(emf);
        {
            List<Note> notes = noteDao.readAll();

            // TODO: use the tag resolver

            SetMultimap<TagChoice, Tag> selectedTags = settings.getSelectedTags();
            Set<Tag> showTags = selectedTags.get(TagChoice.SHOW);
            Set<Tag> hideTags = selectedTags.get(TagChoice.HIDE);
            for (Note note : notes) {
                Set<Tag> tags = note.getTags();
                if (!showTags.isEmpty() && Sets.intersection(showTags, tags).isEmpty()) {
                    continue;
                }
                if (!showTags.isEmpty() && !Sets.intersection(hideTags, tags).isEmpty()) {
                    continue;
                }
                update.addVertex(note);
            }
        }

        // TODO: choose relevant relator
        final Relator relator = new TagRelator();
        relator.refresh(emf);

        Collection<Note> notes = update.getVertices();
        final List<Weight> edges = Lists.newArrayList();
        Convenience.upperOuter(notes, new Loop<Note>() {
            @Override
            public void action(Note first, Note second) {
                double weight = relator.relate(first, second);
                if (weight > 0 && weight < 1) {
                    // let the clusterer deal with exact weights
                    Weight edge = new Weight(weight);
                    edges.add(edge);
                    update.addEdge(edge, first, second);
                }
            }
        });
        Collections.sort(edges);
        int connections = settings.getConnections();
        // not Set to avoid degeneracy
        List<Weight> keepers = Lists.newArrayList(Iterables.limit(edges, connections));
        for (Weight edge : edges) {
            if (!keepers.contains(edge)) {
                update.removeEdge(edge);
            }
        }

        update(update);

        Set<Set<Note>> clusters = Sets.newHashSet();
        for (Set<Note> cluster : relator.cluster(notes)) {
            if (cluster.size() > 0) {
                clusters.add(cluster);
            }
        }
        // TODO: check if the clusters have actually changed
        fireClustersChanged(clusters);

        log.info(update.getVertexCount() + " vertices, " + update.getEdgeCount() + " edges, " + clusters.size() + " clusters");

        // TODO: check if the bunch selections have actually changed
        fireSelectedBunchesChanged();
    }

    // updates the 'graph' object, with minimal changes, to match the parameter
    private void update(final UndirectedSparseGraph<Note, Weight> update) {
        Collection<Note> newVertices = update.getVertices();
        {
            Collection<Note> oldVertices = Sets.newHashSet(graph.getVertices());
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

    public enum TagChoice {

        SHOW, HIDE

    }

    /**
     * @param choice
     * @param tag
     */
    public void selectTag(Tag tag, @Nullable TagChoice choice) {
        Preconditions.checkNotNull(tag);
        SetMultimap<TagChoice, Tag> selected = settings.getSelectedTags();

        for (TagChoice key : TagChoice.values()) {
            selected.remove(key, tag);
        }
        if (choice != null) {
            selected.put(choice, tag);
        }

        fireTagSelectionChanged(selected);

        doRefresh();
    }

    public Collection<Bunch> getBunches() {
        BunchDao dao = new BunchDao(emf);
        return dao.readAll();
    }

    public void newBunch(Collection<Note> notes) {
        Preconditions.checkNotNull(notes);
        Preconditions.checkArgument(!notes.isEmpty());

        Bunch bunch = new Bunch();
        bunch.setTitle("New Bunch");
        BunchDao dao = new BunchDao(emf);
        dao.create(bunch);

        fireBunchesChanged(dao);
    }

    public void addToBunch(Bunch bunch, Note note) {
        Preconditions.checkNotNull(bunch);
        Preconditions.checkNotNull(note);

        BunchDao dao = new BunchDao(emf);
        Set<Note> notes = bunch.getNotes();
        notes.add(note);
        bunch.setNotes(notes);
        dao.update(bunch);

        fireBunchesChanged(dao);
    }

    public void selectBunch(Bunch bunch, boolean select) {
        boolean change = false;
        if (!select) {
            change = settings.getSelectedBunches().remove(bunch.getId());
        } else {
            change = settings.getSelectedBunches().add(bunch.getId());
        }
        if (change) {
            fireSelectedBunchesChanged();
        }
    }

    private void fireBunchesChanged(BunchDao dao) {
        List<Bunch> bunches = dao.readAll();
        fireBunchesChanged(bunches);
    }

    private void fireSelectedBunchesChanged() {
        BunchDao dao = new BunchDao(emf);
        Collection<Bunch> bunches = dao.read(settings.getSelectedBunches());
        fireSelectedBunchesChanged(bunches);
    }

    /**
     * Keeps all the persistent user settings in one place.
     *
     * TODO: API review to persist across sessions and expose listeners
     */
    @Data
    public static class Settings {

        private int connections = 500;

        private String search = "";

        private final SetMultimap<TagChoice, Tag> selectedTags = HashMultimap.create();

        private final Set<Long> selectedBunches = Sets.newLinkedHashSet();

        private final Map<UUID, Importer> importers = Maps.newHashMap();

        private final List<Relator> relators = Lists.newArrayList();

    }
}
