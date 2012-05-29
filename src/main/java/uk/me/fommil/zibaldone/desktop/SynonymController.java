/*
 * Created 29-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.base.Preconditions;
import com.google.common.collect.ListMultimap;
import java.util.List;
import uk.me.fommil.zibaldone.Synonym;

/**
 * Specialist MVC Controller for {@link Synonym} actions.
 * 
 * @author Samuel Halliday
 */
public class SynonymController {

    /**
     * @return all active tag synonyms, indexed by type.
     */
    public ListMultimap<Synonym.Context, String> getSynonyms() {
        // TODO: implement method
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Add a user-defined synonym, possibly merging existing synonyms
     * together.
     * <p>
     * The caller is advised to query {@link #getSynonyms()} immediately.
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
     * The caller is advised to query {@link #getSynonyms()} immediately.
     * 
     * @param tags
     */
    public void removeSynonym(List<String> tags) {
        Preconditions.checkNotNull(tags);

        // TODO: implement method
        throw new UnsupportedOperationException("not implemented yet");
    }
}
