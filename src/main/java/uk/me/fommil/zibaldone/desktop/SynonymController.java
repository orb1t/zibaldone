/*
 * Created 29-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.base.Preconditions;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import uk.me.fommil.zibaldone.Relator;
import uk.me.fommil.zibaldone.Synonym;
import uk.me.fommil.zibaldone.Tag;
import uk.me.fommil.zibaldone.persistence.SynonymDao;

/**
 * Specialist MVC Controller for {@link Synonym} actions.
 * 
 * @author Samuel Halliday
 */
public class SynonymController {

    private final EntityManagerFactory emf;

    /**
     * @param emf
     */
    public SynonymController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * @return all active tag synonyms, indexed by type.
     */
    public ListMultimap<Synonym.Context, Tag> getSynonymTags() {
        // TODO: implement method
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * @return all synonyms relevant to the {@link Relator}s: i.e. manual plus
     * automatic, minus ignored.
     */
    public List<Synonym> getActiveSynonyms() {
        SynonymDao dao = new SynonymDao(emf);
        return Lists.newArrayList(dao.readActive().values());
    }

    /**
     * Add a user-defined synonym, possibly merging existing synonyms
     * together.
     * <p>
     * The caller is advised to query {@link #getSynonymTags()} immediately.
     * 
     * @param tags
     */
    public void addSynonym(List<String> tags) {
        Preconditions.checkNotNull(tags);

        // TODO: implement method
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Remove a user-defined synonym or ignore an automatic one.
     * A call to this followed by
     * {@link #addSynonym(List)} is the recommended procedure in which
     * to modify an existing user-defined synonym.
     * <p>
     * The caller is advised to query {@link #getSynonymTags()} immediately.
     * 
     * @param tags
     */
    public void removeSynonym(List<String> tags) {
        Preconditions.checkNotNull(tags);

        // TODO: implement method
        throw new UnsupportedOperationException("not implemented yet");
    }
}
