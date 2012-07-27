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
import lombok.AutoGenMethodStub;
import lombok.Getter;
import lombok.ListenerSupport;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import uk.me.fommil.utils.Convenience;
import uk.me.fommil.utils.Convenience.Loop;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Relator;
import uk.me.fommil.zibaldone.Tag;
import uk.me.fommil.zibaldone.control.Listeners.ClusterId;
import uk.me.fommil.zibaldone.control.Listeners.ClusterListener;
import uk.me.fommil.zibaldone.control.Listeners.NoteListener;
import uk.me.fommil.zibaldone.control.Listeners.TagListener;
import uk.me.fommil.zibaldone.control.TagController.TagChoice;
import uk.me.fommil.zibaldone.persistence.NoteDao;
import uk.me.fommil.zibaldone.relator.TagRelator;

/**
 * Controller for the visual graph using JUNG as the backend.
 * <p>
 * The View primarily asynchronous updates via {@link ObservableGraph}
 * and {@link Listeners}.
 * <p>
 * The JUNG graph has {@link Note}s on vertices and {@link Weight} values
 * {@code [0, 1]}, as defined in the {@link Relator}, on the edges. User
 * preferences result in the graph being trimmed to make computationally
 * tractable the rendering of larger graphs.
 * <p>
 * This must be registered with anything that changes {@link Tag}s or
 * {@link Note}s.
 * 
 * @author Samuel Halliday
 */
@Log
@RequiredArgsConstructor
@ListenerSupport(ClusterListener.class)
@AutoGenMethodStub
public class GraphController implements TagListener, NoteListener {

    @NonNull
    private final EntityManagerFactory emf;

    @NonNull
    private final Settings settings;

    @Getter
    private final ObservableGraph<Note, Weight> graph = new ObservableGraph<Note, Weight>(new UndirectedSparseGraph<Note, Weight>());

    // ??: it's a shame there is no Listener interface for Collections
    private final Map<ClusterId, Set<Note>> clusters = Maps.newHashMap();

    // TODO: user choice of Relator
    private final Relator relator = new TagRelator();
    
    @Override
    public void notesAdded(Set<Note> notes) {
        Preconditions.checkNotNull(notes);
        Preconditions.checkArgument(!notes.isEmpty());

        rebuildVertices(notes);

        relator.refresh(emf);
        rebuildEdges(relator);
        rebuildClusters(relator);
    }

    @Override
    public void notesRemoved(Set<Note> notes) {
        Preconditions.checkNotNull(notes);
        Preconditions.checkArgument(!notes.isEmpty());

        for (Note note : notes) {
            graph.removeVertex(note);
        }
        relator.refresh(emf);
        rebuildEdges(relator);
    }

    @Override
    public void tagSelection(Tag tag, TagChoice choice) {
        Preconditions.checkNotNull(tag);
        Preconditions.checkNotNull(choice);

        // this essentially involves a full rebuild of 'graph'
        rebuildVertices();
        relator.refresh(emf);
        rebuildEdges(relator);
        rebuildClusters(relator);
    }

    private void rebuildVertices() {
        NoteDao dao = new NoteDao(emf);
        Set<Note> notes = Sets.newHashSet(dao.readAll());
        List<Note> existing = Lists.newArrayList(graph.getVertices());
        for (Note note : existing) {
            if (!notes.contains(note)) {
                graph.removeVertex(note);
            }
        }
        rebuildVertices(notes);
    }

    // add notes to the graph, subject to filter rules
    private void rebuildVertices(Set<Note> notes) {
        Preconditions.checkNotNull(notes);
        Preconditions.checkArgument(!notes.isEmpty());

        Map<Tag, TagChoice> selections = settings.getSelectedTags();
        boolean restricting = selections.values().contains(TagChoice.SHOW);

        for (Note note : notes) {
            if (showVertex(note, selections, restricting)) {
                graph.addVertex(note);
            } else if (graph.containsVertex(note)) {
                graph.removeVertex(note);
            }
        }
    }

    private boolean showVertex(Note note, Map<Tag, TagChoice> selections, boolean restricting) {
        Set<Tag> tags = note.getTags();
        boolean show = false;
        for (Tag tag : tags) {
            // TODO: tag resolution
            TagChoice selection = selections.get(tag);
            if (selection == TagChoice.HIDE) {
                return false;
            }
            if (selection == TagChoice.SHOW) {
                show = true;
            }
        }
        return !restricting || show;
    }

    private void rebuildEdges(final Relator relator) {
        // use a tmp to build up the 'full' graph and then trim it
        // to avoid listeners from being given unnecessary changes
        Collection<Note> notes = graph.getVertices();
        final Graph<Note, Weight> tmp = new UndirectedSparseGraph<Note, Weight>();
        for (Note note : notes) {
            tmp.addVertex(note);
        }

        final List<Weight> allEdges = Lists.newArrayList();
        Convenience.upperOuter(notes, new Loop<Note>() {
            @Override
            public void action(Note first, Note second) {
                double weight = relator.relate(first, second);
                if (weight > 0 && weight < 1) {
                    Weight edge = new Weight(weight);
                    allEdges.add(edge);
                    tmp.addEdge(edge, first, second);
                }
            }
        });


        Collections.sort(allEdges);
        int connections = settings.getConnections();
        // not Set to avoid degeneracy
        List<Weight> trimmedEdges = Lists.newArrayList(Iterables.limit(allEdges, connections));
        for (Weight edge : allEdges) {
            if (!trimmedEdges.contains(edge)) {
                tmp.removeEdge(edge);
            }
        }

        Convenience.upperOuter(notes, new Loop<Note>() {
            @Override
            public void action(Note first, Note second) {
                Weight oldWeight = graph.findEdge(first, second);
                Weight newWeight = tmp.findEdge(first, second);
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

    private void rebuildClusters(final Relator relator) {
        Collection<Note> notes = graph.getVertices();
        Set<Set<Note>> newClusters = Sets.newHashSet();
        for (Set<Note> cluster : relator.cluster(notes)) {
            if (cluster.size() > 1) {
                newClusters.add(cluster);
            }
        }
        Set<ClusterId> matched = Sets.newHashSet();

        for (Set<Note> newCluster : newClusters) {
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
}
