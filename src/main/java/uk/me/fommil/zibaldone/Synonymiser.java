/*
 * Created 07-Aug-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import uk.me.fommil.utils.Lucene;
import uk.me.fommil.zibaldone.persistence.NoteDao;
import uk.me.fommil.zibaldone.persistence.SynonymDao;

/**
 * Refreshes the list of automatic {@link Synonym}s.
 *
 * @author Samuel Halliday
 */
@Log
@RequiredArgsConstructor
public class Synonymiser {

    private final EntityManagerFactory emf;

    // TODO: automatically generate synonyms using a Lucene/other dataset
    public void refresh() {
        NoteDao dao = new NoteDao(emf);
        Set<Tag> tags = dao.getAllTags();
        log.info(tags.size() + " unique Tags: " + tags);

        HashMultimap<Tag, Tag> stems = HashMultimap.create();
        for (Tag tag : tags) {
            Tag stem = new Tag();
            stem.setText(Lucene.tokeniseAndStem(Lucene.removeStopWords(tag.getText())));
            stems.put(stem, tag);
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
