/*
 * Created 13-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.relator;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import uk.me.fommil.utils.GuruMeditationFailure;
import uk.me.fommil.zibaldone.Equivalence;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Relator;
import uk.me.fommil.zibaldone.Tag;

/**
 * Defines the relation between {@link Note} instances based purely on tags,
 * whilst respecting the {@link Equivalence} rules.
 * 
 * @author Samuel Halliday
 */
public class TagRelator implements Relator {

    private static final Logger log = Logger.getLogger(TagRelator.class.getName());

    // tags that appear in an Equivalence are resolved to an arbitrary tag
    private final Map<Tag, Tag> resolve = Maps.newHashMap();

    /**
     * Assumes that the provided {@link Equivalence} instances do not change
     * after construction and also ignores their type - so every
     * {@link Equivalence} is treated as valid (even if it is actually an
     * ignore instruction).
     * 
     * @param equivalences
     */
    public TagRelator(List<Equivalence> equivalences) {

        // imagine situation where user-defined equivalences extend
        // each other and automatic ones

        Set<Set<Tag>> bags = Sets.newHashSet();
        for (Equivalence e : equivalences) {
            Set<Tag> tags = e.getTags();
            log.info(tags.toString());
            bags.add(tags);
        }

        bags = distinctBags(bags);
        log.info(bags.toString());

        // TODO: create the resolver map
    }

    @Override
    public double relate(Note a, Note b) {
        Preconditions.checkNotNull(a);
        Preconditions.checkNotNull(b);
        if (a.equals(b)) {
            return 0;
        }




        throw new UnsupportedOperationException("Not supported yet.");
    }

    private Tag resolve(Tag tag) {
        if (resolve.containsKey(tag)) {
            return resolve.get(tag);
        }
        return tag;
    }

    // ensure that there are no overlapping Tags in the bags
    // by merging Tag bags of non-zero intersection
    // TODO: find a more elegant way to do this
    private Set<Set<Tag>> distinctBags(Set<Set<Tag>> bags) {
        Set<Set<Tag>> reduced = distinctBags1(bags);
        // imagine N bags "chained" together with one tag in common with the one
        // in front and the one behind. Every merge will about half the number
        // of bags.
        for (int i = 0; i < bags.size() ; i++) {
            Set<Set<Tag>> reducedMore = distinctBags1(reduced);
            if (reducedMore.equals(reduced)) {
                return reducedMore;
            }
            reduced = reducedMore;
        }
        throw new GuruMeditationFailure();
    }

    private Set<Set<Tag>> distinctBags1(Set<Set<Tag>> bags) {
        Set<Set<Tag>> distinct = Sets.newHashSet();
        for (Set<Tag> bag1 : bags) {
            boolean merged = false;
            for (Set<Tag> bag2 : bags) {
                if (bag1 == bag2) {
                    continue;
                }
                if (!Sets.intersection(bag1, bag2).isEmpty()) {
                    distinct.add(Sets.union(bag1, bag2));
                    merged = true;
                }
            }
            if (!merged) {
                distinct.add(bag1);
            }
        }
        return distinct;
    }
}
