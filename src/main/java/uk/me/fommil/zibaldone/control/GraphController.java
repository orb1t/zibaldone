/*
 * Created 28-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.control;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManagerFactory;
import lombok.AutoGenMethodStub;
import lombok.Cleanup;
import lombok.Getter;
import lombok.ListenerSupport;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import uk.me.fommil.utils.Convenience;
import uk.me.fommil.utils.Convenience.Loop;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Relator;
import uk.me.fommil.zibaldone.Tag;
import uk.me.fommil.zibaldone.control.Listeners.ClusterId;
import uk.me.fommil.zibaldone.control.Listeners.ClusterListener;
import uk.me.fommil.zibaldone.control.Listeners.NoteListener;
import uk.me.fommil.zibaldone.control.Listeners.SearchListener;
import uk.me.fommil.zibaldone.control.Listeners.TagListener;
import uk.me.fommil.zibaldone.control.TagController.TagChoice;
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
public class GraphController implements TagListener, NoteListener, SearchListener {

    @NonNull
    private final EntityManagerFactory emf;

    @NonNull
    private final Settings settings;

    @Getter
    private final ObservableGraph<Note, Weight> graph = new ObservableGraph<Note, Weight>(new UndirectedSparseGraph<Note, Weight>());

    private final Map<ClusterId, Set<Note>> clusters = Maps.newHashMap();

    @Getter @Setter @NonNull
    private Relator relator = new TagRelator();

    // keys are the UUIDs of the Notes, and are used in Lucene's Documents
    private final BiMap<String, Note> noteIds = HashBiMap.create();

    private final RAMDirectory index = new RAMDirectory();

    private final StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);

    @Override
    public void notesChanged(Set<Note> notes) {
        Preconditions.checkNotNull(notes);
        Preconditions.checkArgument(!notes.isEmpty());

        // the new Notes might be different, but will still be "equal"
        // so remove/add the vertices which have changed (no "update" in JUNG)
        for (Note existing : Lists.newArrayList(graph.getVertices())) {
            if (notes.contains(existing)) {
                Note update = Iterables.find(notes, Predicates.equalTo(existing));
                if (!existing.propertiesEquals(update)) {
                    graph.removeVertex(existing);
                    graph.addVertex(update);
                }
            }
        }

        rebuildLucene(notes);
        rebuildGraph();
    }

    @Override
    public void tagSelection(Tag tag, TagChoice choice) {
        rebuildGraph();
    }

    @Override
    public void searchChanged(String search) {
        settings.setSearch(search);
        rebuildGraph();
    }

    private void rebuildLucene(Set<Note> notes) {
        try {
            // rebuild the Lucene search objects
            // see http://www.lucenetutorial.com/lucene-in-5-minutes.html
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);
//            config.setMaxBufferedDocs(Math.max(1000, notes.size() + 1));
//            config.setRAMBufferSizeMB(IndexWriterConfig.DISABLE_AUTO_FLUSH);
            @Cleanup IndexWriter w = new IndexWriter(index, config);
            w.deleteAll();
            w.commit();
            noteIds.clear();
            for (Note note : notes) {
                Document doc = new Document();
                String id = note.getId().toString();
                doc.add(new Field("id", id, Field.Store.YES, Field.Index.NO));
                doc.add(new Field("title", note.getTitle(), Field.Store.YES, Field.Index.ANALYZED));
                doc.add(new Field("tags", Joiner.on(" ").join(note.getTags()), Field.Store.YES, Field.Index.ANALYZED));
                doc.add(new Field("contents", note.getContents(), Field.Store.YES, Field.Index.ANALYZED));
                w.addDocument(doc);
                noteIds.put(id, note);
            }
            w.commit();
        } catch (IOException e) {
            throw new IllegalStateException("Lucene stopped working", e);
        }
    }

    private Set<Note> searchRestrictedNotes() {
        String search = settings.getSearch();
        if (search == null || search.trim().length() < 4 || noteIds.isEmpty()) {
            return noteIds.values();
        }
        try {
            Set<Note> notes = Sets.newHashSet();
            MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_36,
                    new String[]{"title", "tags", "contents"}, analyzer);
            Query q;
            try {
                if (search.contains("content:")) {
                    search = search.replace("content:", "contents:");
                }
                if (search.contains("tag:")) {
                    search = search.replace("tag:", "tags:");
                }
                q = parser.parse(search);
            } catch (ParseException e) {
                log.fine("Lucene didn't like search: \"" + search + "\"");
                return Sets.newHashSet(graph.getVertices());
            }

            @Cleanup IndexReader reader = IndexReader.open(index);
            @Cleanup IndexSearcher searcher = new IndexSearcher(reader);
            TopScoreDocCollector collector = TopScoreDocCollector.create(noteIds.size(), true);
            searcher.search(q, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;
            for (int i = 0; i < hits.length; i++) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                Note note = noteIds.get(d.get("id"));
                notes.add(note);
            }
            return notes;
        } catch (IOException e) {
            throw new IllegalStateException("Lucene stopped working", e);
        }
    }

    private void rebuildGraph() {
        rebuildVertices();
        relator.refresh(emf);
        rebuildEdges();
        rebuildClusters();
    }

    private void rebuildVertices() {
        Set<Note> notes = searchRestrictedNotes();

        for (Note existing : Lists.newArrayList(graph.getVertices())) {
            if (!notes.contains(existing)) {
                graph.removeVertex(existing);
            }
        }

        Map<Tag, TagChoice> selections = settings.getSelectedTags();
        boolean restricting = selections.values().contains(TagChoice.SHOW);

        for (Note note : notes) {
            if (showVertexTagRules(note, selections, restricting)) {
                graph.addVertex(note);
            } else if (graph.containsVertex(note)) {
                graph.removeVertex(note);
            }
        }
    }

    private boolean showVertexTagRules(Note note, Map<Tag, TagChoice> selections, boolean restricting) {
        boolean show = !restricting;
        Set<Tag> tags = note.getTags();
        for (Tag tag : tags) {
            TagChoice selection = selections.get(tag);
            if (selection == TagChoice.SHOW) {
                return true;
            }
            if (selection == TagChoice.HIDE) {
                show = false;
            }
        }
        return show;
    }

    private void rebuildEdges() {
        // use a tmp to build up the 'full' graph and then trim it
        // to avoid listeners from being given unnecessary changes
        // prior to pruning
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

        // List doesn't get confused about equality/compareTo
        Collections.sort(allEdges);
        int connections = settings.getConnections();
        Set<Weight> trimmedEdges = Sets.newHashSet(Iterables.limit(allEdges, connections));
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

    private void rebuildClusters() {
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
