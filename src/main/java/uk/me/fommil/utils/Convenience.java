/*
 * Created 25-Jun-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.utils;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.intersection;
import static com.google.common.collect.Sets.newHashSet;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Arbitrary convenience methods for working with a variety of standard Java
 * types.
 *
 * @author Samuel Halliday
 */
public final class Convenience {

    /**
     * @param <T>
     * @see #selfLoop(List, SelfLoop) 
     */
    public interface SelfLoop<T> {

        /**
         * @param first
         * @param second
         */
        public void action(T first, T second);
    }

    /**
     * Perform a two-level loop over a list, missing out the self and
     * anti-symmetric index combinations of elements. Avoids having to
     * introduce explicit indices.
     * 
     * @param <T>
     * @param list
     * @param operation
     */
    public static <T> void selfLoop(List<T> list, SelfLoop<T> operation) {
        Preconditions.checkNotNull(list);
        Preconditions.checkNotNull(operation);
        for (int i = 0; i < list.size(); i++) {
            T first = list.get(i);
            for (int j = i + 1; j < list.size(); j++) {
                T second = list.get(j);
                operation.action(first, second);
            }
        }
    }

    /**
     * Useful for ensuring that there are no empty collections.
     */
    public static final Predicate<Collection<?>> NO_EMPTIES = new Predicate<Collection<?>>() {

        @Override
        public boolean apply(Collection<?> input) {
            if (input == null || input.isEmpty()) {
                return false;
            }
            return true;
        }
    };

    /**
     * Given a collection of sets, return the set of distinct sets:
     * i.e. merge all sets which share a common entry.
     *
     * @param <T>
     * @param sets
     * @see <a href="http://stackoverflow.com/questions/10634408/">Related discussion on Stack Overflow</a>
     * @return
     */
    public static <T> Set<Set<T>> disjointify(Collection<Set<T>> sets) {
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
}
