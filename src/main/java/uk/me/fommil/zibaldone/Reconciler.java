/*
 * Created 08-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import javax.persistence.EntityManagerFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.tartarus.snowball.SnowballProgram;
import org.tartarus.snowball.ext.EnglishStemmer;
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

    private final SnowballProgram stemmer = new EnglishStemmer();

    @NonNull
    private final EntityManagerFactory emf;

    /**
     * Convenience method for {@link #reconcile(java.util.Map)}.
     * 
     * @param sourceId the proposed {@link Note#setSource(String)}
     * @param notes
     */
    public void reconcile(UUID sourceId, List<Note> notes) {
        Preconditions.checkNotNull(sourceId);
        Preconditions.checkNotNull(notes);
        Map<UUID, List<Note>> singleton = Collections.singletonMap(sourceId, notes);
        reconcile(singleton);
    }

    /**
     * Attempts to reconcile all the given {@link Note}s with those currently
     * persisted. {@link Note#getSource()} will be ignored.
     * 
     * @param incoming indexed by the proposed {@link Note#setSource(UUID)}.
     */
    // @Synchronized TODO http://netbeans.org/bugzilla/show_bug.cgi?id=215078
    public synchronized void reconcile(Map<UUID, List<Note>> incoming) {
        Preconditions.checkNotNull(incoming);

        NoteDao dao = new NoteDao(emf);
        long start = dao.count();
        for (Map.Entry<UUID, List<Note>> entry : incoming.entrySet()) {
            UUID sourceId = entry.getKey();
            log.info("Reconciling: " + sourceId);

            List<Note> notes = entry.getValue();

            if (dao.countForImporter(sourceId) == 0) {
                for (Note note : notes) {
                    note.setSource(sourceId);
                }
                dao.create(notes);
            } else {
                // TODO: implement reconciliation
                throw new UnsupportedOperationException("not implemented yet");
            }
        }
        long end = dao.count();
        log.info("Persisted " + (end - start) + " Notes");

        Set<Tag> tags = dao.getAllTags();
        log.info(tags.size() + " unique Tags: " + tags);
        HashMultimap<Tag, Tag> stems = HashMultimap.create();

        for (Tag tag : tags) {
            Tag stem = tokeniseAndStem(tag);
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

    private Tag tokeniseAndStem(Tag tag) {
        // multi-token stemming is never going to be brilliant
        // e.g. consider "every thing" and "everything" - they might not
        // have the same stem.
        StringBuilder builder = new StringBuilder();
        String text = tag.getText();
        StringTokenizer tokeniser = new StringTokenizer(text);
        while (tokeniser.hasMoreTokens()) {
            String token = tokeniser.nextToken().toLowerCase();
            String stemmed = stem(token);
            builder.append(stemmed);
        }

        Tag stem = new Tag();
        stem.setText(builder.toString());

        return stem;
    }

    private String stem(String word) {
        stemmer.setCurrent(word);
        stemmer.stem();
        return stemmer.getCurrent();
    }
}
