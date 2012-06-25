/*
 * Created 08-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import org.tartarus.snowball.SnowballProgram;
import org.tartarus.snowball.ext.EnglishStemmer;
import uk.me.fommil.zibaldone.persistence.SynonymDao;
import uk.me.fommil.zibaldone.persistence.NoteDao;

/**
 * Identifies when a newly imported {@link Note} is actually an update to an
 * existing one. This class is responsible for persisting {@link Note} instances
 * and updating the library of {@link Tag} stems.
 * 
 * @author Samuel Halliday
 */
public class Reconciler {

    private static final Logger log = Logger.getLogger(Reconciler.class.getName());

    private final SnowballProgram stemmer = new EnglishStemmer();

    private final EntityManagerFactory emf;

    /**
     * @param emf
     */
    public Reconciler(EntityManagerFactory emf) {
        Preconditions.checkNotNull(emf);
        this.emf = emf;
    }

    /**
     * Convenience method for {@link #reconcile(java.util.Map)}.
     * 
     * @param importer
     * @param notes
     */
    public void reconcile(Importer importer, List<Note> notes) {
        Preconditions.checkNotNull(importer);
        Preconditions.checkNotNull(notes);
        Map<Importer, List<Note>> singleton = Collections.singletonMap(importer, notes);
        reconcile(singleton);
    }

    /**
     * Attempts to reconcile all the given {@link Note}s with those currently
     * persisted.
     * 
     * @param all ids of the notes will be ignored
     */
    public synchronized void reconcile(Map<Importer, List<Note>> all) {
        Preconditions.checkNotNull(all);

        NoteDao dao = new NoteDao(emf);
        long start = dao.count();
        for (Entry<Importer, List<Note>> entry : all.entrySet()) {
            String name = entry.getKey().getInstanceName();
            log.info("Reconciling: " + name);

            List<Note> notes = entry.getValue();

            if (dao.countForImporter(name) == 0) {
                for (int i = 0; i < notes.size(); i++) {
                    NoteId id = new NoteId();
                    id.setId((long) i);
                    id.setSource(name);
                    notes.get(i).setId(id);
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
        Multimap<Tag, Tag> stems = ArrayListMultimap.create();

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
            Set<Tag> originals = Sets.newHashSet(stems.get(stem));
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
