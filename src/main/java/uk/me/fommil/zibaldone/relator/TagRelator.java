/*
 * Created 13-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.relator;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import com.google.common.collect.Iterables;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import static com.google.common.collect.Sets.intersection;
import static com.google.common.collect.Sets.newHashSet;
import java.util.*;
import java.util.logging.Logger;
import uk.me.fommil.zibaldone.Synonym;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Relator;
import uk.me.fommil.zibaldone.Tag;

/**
 * Defines the relation between {@link Note} instances based purely on tags,
 * whilst respecting the {@link Synonym} rules.
 * 
 * @author Samuel Halliday
 */
public class TagRelator implements Relator {

    private static final Logger log = Logger.getLogger(TagRelator.class.getName());

    // tags that appear in an Synonym are resolved to an arbitrary tag
    private final Map<Tag, Tag> resolve = Maps.newHashMap();

    /**
     * Assumes that the provided {@link Synonym} instances do not change
     * after construction and also ignores their type - so every
     * {@link Synonym} is treated as valid (even if it is actually an
     * ignore instruction).
     * 
     * @param synonyms
     */
    public TagRelator(List<Synonym> synonyms) {
        Set<Set<Tag>> bags = Sets.newHashSet();
        for (Synonym e : synonyms) {
            Set<Tag> tags = e.getTags();
            bags.add(tags);
        }
        for (Set<Tag> bag : disjointify(bags)) {
            Tag resolved = Iterables.get(bag, 0);
            for (Tag tag : bag) {
                resolve.put(tag, resolved);
            }
        }
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

    // ensure that there are no overlapping Set<T>s
    // by merging Set<T>s of non-zero intersection
    // note the underlying sets may be modified
    private <T> Set<Set<T>> disjointify(Collection<Set<T>> sets) {
        List<Set<T>> disjoint = newArrayList(sets);
        for (Set<T> set1 : disjoint) {
            for (Set<T> set2 : filter(disjoint, not(equalTo(set1)))) {
                if (!intersection(set1, set2).isEmpty()) {
                    // this wouldn't be safe for a Set<Set<T>>
                    set1.addAll(set2);
                    set2.clear();
                }
            }
        }
        return newHashSet(filter(disjoint, NO_EMPTIES));
    }
    private static final Predicate<Set<?>> NO_EMPTIES = new Predicate<Set<?>>() {

        @Override
        public boolean apply(Set<?> input) {
            if (input == null || input.isEmpty()) {
                return false;
            }
            return true;
        }
    };

}
