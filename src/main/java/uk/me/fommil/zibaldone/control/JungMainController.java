/*
 * Created 28-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.control;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManagerFactory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ListenerSupport;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import uk.me.fommil.utils.Convenience;
import uk.me.fommil.utils.Convenience.Loop;
import uk.me.fommil.zibaldone.Bunch;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Relator;
import uk.me.fommil.zibaldone.Tag;
import uk.me.fommil.zibaldone.control.Listeners.BunchesChangedListener;
import uk.me.fommil.zibaldone.control.Listeners.ClusterId;
import uk.me.fommil.zibaldone.control.Listeners.ClustersChangedListener;
import uk.me.fommil.zibaldone.control.Listeners.TagsChangedListener;
import uk.me.fommil.zibaldone.persistence.BunchDao;
import uk.me.fommil.zibaldone.persistence.NoteDao;
import uk.me.fommil.zibaldone.relator.TagRelator;

/**
 * The main MVC Controller that uses JUNG to organise the core Zibaldone
 * objects (the Model).
 * <p>
 * The View primarily receives asynchronous updates via {@link ObservableGraph}
 * and {@link Listeners}.
 * <p>
 * The JUNG graph has {@link Note}s on vertices and {@link Weight} values
 * {@code [0, 1]}, as defined in the {@link Relator}, on the edges. User
 * preferences result in the graph being trimmed to make computationally
 * tractable the rendering of larger graphs.
 * 
 * @author Samuel Halliday
 */
@Log
@RequiredArgsConstructor

@ListenerSupport({BunchesChangedListener.class, ClustersChangedListener.class, TagsChangedListener.class})
public class JungMainController {

    @NonNull @Getter(AccessLevel.PROTECTED)
    private final EntityManagerFactory emf;

    @Getter
    private final ObservableGraph<Note, Weight> graph = new ObservableGraph<Note, Weight>(new UndirectedSparseGraph<Note, Weight>());

    @Getter
    private final Settings settings = new Settings();

    // ??: it's a shame there is no Listener interface for Collections
    private final Map<ClusterId, Set<Note>> clusters = Maps.newHashMap();

    /**
     * Updates the underlying JUNG model based on current settings.
     */
    public void doRefresh() {
        // TODO: select Relator
        // TODO: use the tag resolver in TagRelator
        Relator relator = new TagRelator();
        relator.refresh(emf);

        Set<Note> notes = getSelectedNotes();
        Graph<Note, Weight> updatedGraph = buildGraph(notes, relator);
        updateGraph(updatedGraph);

        Set<Set<Note>> updatedClusters = buildClusters(relator);
        updateClusters(updatedClusters);

        log.info(graph.getVertexCount() + " vertices, " + graph.getEdgeCount() + " edges, " + clusters.size() + " clusters");
    }

    private Set<Note> getSelectedNotes() {
        NoteDao noteDao = new NoteDao(emf);
        List<Note> allNotes = noteDao.readAll();
        Set<Note> notes = Sets.newHashSet();

        SetMultimap<TagChoice, Tag> selectedTags = settings.getSelectedTags();
        Set<Tag> showTags = selectedTags.get(TagChoice.SHOW);
        Set<Tag> hideTags = selectedTags.get(TagChoice.HIDE);
        for (Note note : allNotes) {
            Set<Tag> tags = note.getTags();
            if (!showTags.isEmpty() && Sets.intersection(showTags, tags).isEmpty()) {
                continue;
            }
            if (!hideTags.isEmpty() && !Sets.intersection(hideTags, tags).isEmpty()) {
                continue;
            }
            notes.add(note);
        }
        return notes;
    }

    private Graph<Note, Weight> buildGraph(Collection<Note> notes, final Relator relator) {
        final UndirectedSparseGraph<Note, Weight> update = new UndirectedSparseGraph<Note, Weight>();

        for (Note note : notes) {
            update.addVertex(note);
        }

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

        return update;
    }

    // updates the 'graph' object, with minimal changes, to match the parameter
    // and alerts listeners
    private void updateGraph(final Graph<Note, Weight> update) {
        Collection<Note> newVertices = update.getVertices();
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

    private Set<Set<Note>> buildClusters(Relator relator) {
        Collection<Note> notes = graph.getVertices();
        Set<Set<Note>> newClusters = Sets.newHashSet();
        for (Set<Note> cluster : relator.cluster(notes)) {
            if (cluster.size() > 1) {
                newClusters.add(cluster);
            }
        }
        return newClusters;
    }

    // updates the 'clusters' map, with minimal changes, to match the parameter
    // and alerts listeners
    private void updateClusters(Set<Set<Note>> update) {
        Set<ClusterId> matched = Sets.newHashSet();

        for (Set<Note> newCluster : update) {
            ClusterId id = matchCluster(newCluster);
            if (id == null) {
                id = new ClusterId();
                clusters.put(id, newCluster);
                fireClusterAdded(id, newCluster);
            } else {
                if (!clusters.get(id).equals(newCluster)) {
                    clusters.put(id, newCluster);
                    fireClusterUpdated(id, newCluster);
                }
            }
            matched.add(id);
        }

        Iterator<ClusterId> it = clusters.keySet().iterator();
        while (it.hasNext()) {
            ClusterId id = it.next();
            if (!matched.contains(id)) {
                it.remove();
                fireClusterRemoved(id);
            }
        }

        // TODO: internal check that clusters are distinct
    }

    private ClusterId matchCluster(Set<Note> cluster) {
        for (ClusterId id : clusters.keySet()) {
            Set<Note> existing = clusters.get(id);
            if (Sets.intersection(existing, cluster).size() >= 1) {
                return id;
            }
        }
        return null;
    }

    public enum TagChoice {

        IGNORE, SHOW, HIDE

    }

    /**
     * @param choice
     * @param tag
     */
    public void selectTag(Tag tag, TagChoice choice) {
        Preconditions.checkNotNull(tag);
        SetMultimap<TagChoice, Tag> selected = settings.getSelectedTags();

        for (TagChoice key : TagChoice.values()) {
            selected.remove(key, tag);
        }
        if (choice != TagChoice.IGNORE) {
            selected.put(choice, tag);
        }

        fireTagSelectionChanged(selected);

        doRefresh();
    }

    public Collection<Bunch> getBunches() {
        BunchDao dao = new BunchDao(emf);
        return dao.readAll();
    }

    public Bunch newBunch(Set<Note> notes) {
        Preconditions.checkNotNull(notes);
        Preconditions.checkArgument(!notes.isEmpty());

        Bunch bunch = new Bunch();
        bunch.setName("New Bunch");
        bunch.setNotes(notes);
        BunchDao dao = new BunchDao(emf);
        dao.create(bunch);

        fireBunchesChanged(dao);
        return bunch;
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
        boolean change;
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
}
