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
    // underlying set is modified
    private <T> Set<Set<T>> disjointify(Set<Set<T>> sets) {
        for (Set<T> set1 : nestable(sets)) {
            Iterable<Set<T>> others = filter(nestable(sets), not(equalTo(set1)));
            for (Iterator<Set<T>> it = others.iterator(); it.hasNext();) {
                Set<T> set2 = it.next();
                if (!intersection(set1, set2).isEmpty()) {
                    set1.addAll(set2);
                    it.remove();
                }
            }
        }
        return sets;
    }

    private static <T> Iterable<T> nestable(Set<T> set) {
        return new MultiIterable<T>(set);
    }

    /**
     * Provides an Iterable for Sets which allows other instances to remove
     * entries. Very inefficient as it essentially replays the underlying Set's
     * full Iterable for every call to hasNext or next.
     * 
     * @param <T> 
     */
    private static class MultiIterable<T> implements Iterable<T> {

        private final Set<T> collection;

        public MultiIterable(Set<T> collection) {
            this.collection = collection;
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {

                final Set<T> visited = newHashSet();

                volatile T current = null;

                volatile boolean hasNext = false;

                volatile T next = null;

                private void recount() {
                    Iterator<T> it = collection.iterator();
                    hasNext = false;
                    while (it.hasNext()) {
                        T nextTest = it.next();
                        if (visited.contains(nextTest)) {
                            hasNext = true;
                            next = nextTest;
                        }
                    }
                }

                @Override
                public boolean hasNext() {
                    recount();
                    return hasNext;
                }

                @Override
                public T next() {
                    recount();
                    return next;
                }

                @Override
                public void remove() {
                    collection.remove(current);
                }
            };
        }
    }
}
