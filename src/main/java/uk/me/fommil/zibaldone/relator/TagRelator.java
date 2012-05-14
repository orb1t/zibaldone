/*
 * Created 13-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.relator;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.*;
import java.util.logging.Logger;
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
        Set<Set<Tag>> bags = Sets.newHashSet();
        for (Equivalence e : equivalences) {
            Set<Tag> tags = e.getTags();
            bags.add(tags);
        }
        bags = distinctBags(bags);
        for (Set<Tag> bag : bags) {
            Tag resolved = Iterables.get(bag, 0);
            for (Tag tag : bag) {
                resolve.put(tag, resolved);
            }
        }
//        log.info(resolve.toString());
    }

    @Override
    public double relate(Note a, Note b) {
        Preconditions.checkNotNull(a);
        Preconditions.checkNotNull(b);
        Set<Tag> aResolved = resolve(a.getTags());
        Set<Tag> bResolved = resolve(b.getTags());
        if (aResolved.equals(bResolved)) {
            return 0;
        }


        int overlapTags = Sets.intersection(aResolved, bResolved).size();
        if (overlapTags == 0) {
            return 1.0;
        }

        int totalTags = Sets.union(aResolved, bResolved).size();
        // choices choices... how to scale?
        double overlap = (double) overlapTags / totalTags;
        return (1 - overlap);
    }

    private Set<Tag> resolve(Set<Tag> tags) {
        Set<Tag> resolved = Sets.newHashSet();
        for (Tag tag : tags) {
            resolved.add(resolve(tag));
        }
        return resolved;
    }

    private Tag resolve(Tag tag) {
        if (resolve.containsKey(tag)) {
            return resolve.get(tag);
        }
        return tag;
    }

    // ensure that there are no overlapping Tags in the bags
    // by merging Tag bags of non-zero intersection
    // (interesting algorithmically)
    private Set<Set<Tag>> distinctBags(Set<Set<Tag>> bags) {
        List<Set<Tag>> distinct = Lists.newArrayList(bags);
        for (ListIterator<Set<Tag>> it1 = distinct.listIterator(); it1.hasNext();) {
            Set<Tag> bag1 = it1.next();
            for (ListIterator<Set<Tag>> it2 = distinct.listIterator(it1.nextIndex()); it2.hasNext();) {
                Set<Tag> bag2 = it2.next();
                if (!Sets.intersection(bag1, bag2).isEmpty()) {
                    bag1.addAll(bag2);
                    it2.remove();
                }
            }
        }
        return Sets.newHashSet(distinct);
    }
}
