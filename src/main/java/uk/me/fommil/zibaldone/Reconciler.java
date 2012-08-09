/*
 * Created 08-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import javax.persistence.EntityManagerFactory;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import uk.me.fommil.utils.Lucene;
import uk.me.fommil.zibaldone.persistence.NoteDao;

/**
 * Identifies when a newly imported {@link Note} is actually an update to an
 * existing one. This class is responsible for correctly persisting {@link Note}
 * instances from an {@link Importer}.
 * 
 * @author Samuel Halliday
 */
@Log
@RequiredArgsConstructor
public class Reconciler {

    /**
     * Marker interface for an object which implements
     * {@link Object#equals(Object)} and remains invariant
     * across sessions for {@link Note} instances, even if
     * the user changes the {@link Note} at its source.
     */
    public interface Fingerprint {
    }

    /**
     * Simple {@link String} implementation of {@link Fingerprint}.
     */
    @RequiredArgsConstructor
    @Data
    public static class StringFingerprint implements Fingerprint {

        private final String text;

    }

    /**
     * Callback that allows the caller to define the rules
     * for reconciling a {@link Note} against the database.
     * <p>
     * Newly imported Notes can only be reconciled against existing Notes from
     * the same source.
     */
    public interface Callback {

        /**
         * @param newNotes which may not yet be managed by the DB
         * @param candidates
         * @return a one-to-one map from each newNote to either a candidate or itself.
         */
        public BiMap<Note, Note> reconcile(Collection<Note> newNotes, Collection<Note> candidates);

        /**
         * If the fingerprint of a newly imported {@link Note} exactly matches
         * that of one {@link Note} from the database, it will
         * be automatically reconciled. Otherwise
         * {@link #reconcile(Collection, Collection)} will be called.
         * 
         * @param note
         * @return
         */
        public Fingerprint toFingerprint(Note note);
    }

    /**
     * Callback policy that says Notes are always to be considered new, unless
     * their title and tags exactly match one existing Note from the database
     * (up to stop word removal).
     */
    public static final Callback SIMPLE_RECONCILE = new Callback() {
        @Override
        public BiMap<Note, Note> reconcile(Collection<Note> newNotes, Collection<Note> candidates) {
            BiMap<Note, Note> identity = HashBiMap.create();
            for (Note note : newNotes) {
                identity.put(note, note);
            }
            return identity;
        }

        @Override
        public StringFingerprint toFingerprint(Note note) {
            String text = note.getTitle() + " " + Joiner.on(" ").join(Sets.newTreeSet(note.getTags()));
            String summary = Lucene.removeStopWords(text).toLowerCase();
//            log.info(summary);
            return new StringFingerprint(summary);
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
    public void reconcile(UUID sourceId, List<Note> notes, Callback callback) {
        Preconditions.checkNotNull(sourceId);
        Preconditions.checkNotNull(notes);

        NoteDao dao = new NoteDao(emf);
        for (Note note : notes) {
            note.setSource(sourceId);
        }

        log.info("Reconciling: " + sourceId);

        if (dao.countForImporter(sourceId) == 0) {
            long start = dao.count();
            dao.create(notes);
            long end = dao.count();
            log.info("Persisted " + (end - start) + " new notes from " + sourceId);
            return;
        }

        List<Note> existing = dao.readForImporter(sourceId);
        Multimap<Object, Note> summaryMapping = ArrayListMultimap.create();
        for (Note note : existing) {
            summaryMapping.put(callback.toFingerprint(note), note);
        }
        Set<Note> createNotes = Sets.newHashSet();
        Set<Note> updateNotes = Sets.newHashSet();
        Set<Note> deleteNotes = Sets.newHashSet();
        for (Note note : notes) {
            Collection<Note> candidates = summaryMapping.get(callback.toFingerprint(note));
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
                Note update = e.getKey();
                Note old = e.getValue();
                createNotes.remove(update);
                deleteNotes.remove(old);
                update.setId(old.getId());
                updateNotes.add(update);
            }
        }

        log.info("deleting " + deleteNotes.size() + " notes from " + sourceId);
        dao.delete(deleteNotes);
        log.info("creating " + createNotes.size() + " notes from " + sourceId);
        dao.create(createNotes);
        log.info("updating " + updateNotes.size() + " notes from " + sourceId);
        dao.update(updateNotes);
    }
}
