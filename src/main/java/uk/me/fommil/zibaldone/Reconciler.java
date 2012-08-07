/*
 * Created 08-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import javax.persistence.EntityManagerFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.java.Log;
import uk.me.fommil.utils.Lucene;
import uk.me.fommil.zibaldone.persistence.NoteDao;
import uk.me.fommil.zibaldone.persistence.SynonymDao;

/**
 * Identifies when a newly imported {@link Note} is actually an update to an
 * existing one. This class is responsible for persisting {@link Note} instances
 * and updating the library of {@link Tag} stems.
 * 
 * @author Samuel Halliday
 */
@Log
@RequiredArgsConstructor
public class Reconciler {

    /**
     * Callback that allows the user or some other agent to define the rules
     * for reconciling a Note against the existing database. Notes can only be
     * reconciled against the same source, if a Note is moved between importer
     * sources then it cannot be reconciled.
     */
    public interface Reconcile {

        /**
         * @param newNotes which may not yet be managed by the DB
         * @param candidates
         * @return a map from each newNote to either a candidate or itself
         */
        public Map<Note, Note> reconcile(Collection<Note> newNotes, Collection<Note> candidates);

        /**
         * The 'summary' that should be identical for an existing and imported
         * Note if the latter is to be considered an update to the former.
         * (yes, this could be a lot more general than this). Anything that isn't
         * matched by exactly one existing Note will be passed to
         * {@link #reconcile(Collection, Collection)}.
         * 
         * @param note
         * @return
         */
        public String summarise(Note note);
    }

    /**
     * Callback policy that says Notes are always to be considered new, unless
     * their title and tags exactly match an existing one (up to stop word removal).
     */
    public static final Reconcile SIMPLE_RECONCILE = new Reconcile() {
        @Override
        public Map<Note, Note> reconcile(Collection<Note> newNotes, Collection<Note> candidates) {
            Map<Note, Note> identity = Maps.newHashMap();
            for (Note note : newNotes) {
                identity.put(note, note);
            }
            return identity;
        }

        @Override
        public String summarise(Note note) {
            String text = note.getTitle() + " " + Sets.newTreeSet(note.getTags()).toString();
            String summary = Lucene.removeStopWords(text).toLowerCase();
//            log.info(summary);
            return summary;
        }
    };

    @NonNull
    private final EntityManagerFactory emf;

    /**
     * Convenience method for {@link #reconcile(java.util.Map)}.
     * 
     * @param sourceId the proposed {@link Note#setSource(String)}
     * @param notes
     * @param callback 
     */
    public void reconcile(UUID sourceId, List<Note> notes, Reconcile callback) {
        Preconditions.checkNotNull(sourceId);
        Preconditions.checkNotNull(notes);
        Map<UUID, List<Note>> singleton = Collections.singletonMap(sourceId, notes);
        reconcile(singleton, callback);
    }

    /**
     * Attempts to reconcile all the given {@link Note}s with those currently
     * persisted. {@link Note#getSource()} will be ignored.
     * 
     * @param incoming indexed by the proposed {@link Note#setSource(UUID)}.
     * @param callback 
     */
    @Synchronized
    public void reconcile(Map<UUID, List<Note>> incoming, Reconcile callback) {
        Preconditions.checkNotNull(incoming);

        NoteDao dao = new NoteDao(emf);


        for (Map.Entry<UUID, List<Note>> entry : incoming.entrySet()) {
            UUID sourceId = entry.getKey();
            log.info("Reconciling: " + sourceId);

            List<Note> notes = entry.getValue();
            for (Note note : notes) {
                note.setSource(sourceId);
            }

            if (dao.countForImporter(sourceId) == 0) {
                long start = dao.count();
                dao.create(notes);
                long end = dao.count();
                log.info("Persisted " + (end - start) + " Notes");
            } else {
                List<Note> existing = dao.readForImporter(sourceId);
                Multimap<String, Note> summaryMapping = ArrayListMultimap.create();
                for (Note note : existing) {
                    String summary = callback.summarise(note);
                    summaryMapping.put(summary, note);
                }
                Set<Note> createNotes = Sets.newHashSet();
                Set<Note> updateNotes = Sets.newHashSet();
                Set<Note> deleteNotes = Sets.newHashSet();
                for (Note note : notes) {
                    String summary = callback.summarise(note);
                    Collection<Note> candidates = summaryMapping.get(summary);
                    if (candidates.size() == 1) {
                        Note candidate = Iterables.getOnlyElement(candidates);
                        note.setId(candidate.getId());
                        updateNotes.add(note);
                    } else {
                        createNotes.add(note);
                    }
                }

                for (Note note : existing) {
                    if (!createNotes.contains(note) && !updateNotes.contains(note)) {
                        deleteNotes.add(note);
                    }
                }
                Map<Note, Note> reconciled = callback.reconcile(createNotes, deleteNotes);
                Preconditions.checkState(reconciled.size() == createNotes.size());
                Preconditions.checkState(reconciled.size() == Sets.newHashSet(reconciled.values()).size());

                for (Entry<Note, Note> e : reconciled.entrySet()) {
                    if (!e.getKey().equals(e.getValue())) {
                        createNotes.remove(e.getKey());
                        e.getKey().setId(e.getValue().getId());
                        updateNotes.add(e.getKey());
                        deleteNotes.remove(e.getValue());
                        log.info("UPDATING: " + e.getKey());
                    } else {
                        log.info("CREATING: " + e.getKey());
                    }
                }

                log.info("deleting " + deleteNotes.size());
                dao.delete(deleteNotes);
                log.info("creating " + createNotes.size());
                dao.create(createNotes);
                log.info("updating " + updateNotes.size());
                dao.update(updateNotes);
            }
        }

        Set<Tag> tags = dao.getAllTags();
        log.info(tags.size() + " unique Tags: " + tags);
        HashMultimap<Tag, Tag> stems = HashMultimap.create();

        for (Tag tag : tags) {
            Tag stem = new Tag();
            stem.setText(Lucene.tokeniseAndStem(Lucene.removeStopWords(tag.getText())));
            if (!stems.containsKey(stem)) {
                stems.put(stem, tag);
            } else {
                if (!stems.get(stem).contains(tag)) {
                    stems.put(stem, tag);
                }
            }
        }
        log.info(stems.keySet().size() + " unique Stems: " + stems);

        // gather all the stemmed words
        List<Synonym> synonyms = Lists.newArrayList();
        for (Tag stem : stems.keySet()) {
            Set<Tag> originals = stems.get(stem);
            Synonym synonym = new Synonym();
            synonym.setContext(Synonym.Context.AUTOMATIC);
            synonym.setStem(stem);
            synonym.setTags(originals);
            synonyms.add(synonym);
        }
        SynonymDao equivDao = new SynonymDao(emf);
        equivDao.updateAllAutomatics(synonyms);
        log.info(equivDao.count() + " Synonyms: " + synonyms);
    }
}
