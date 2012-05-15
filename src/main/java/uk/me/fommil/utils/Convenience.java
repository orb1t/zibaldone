/*
 * Created 15-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.utils;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Miscellaneous re-usable convenience methods that reduce boilerplate in
 * a variety of places.
 * 
 * @author Samuel Halliday
 */
public final class Convenience {

    /**
     * Sometimes a loop must exclude an element, this methods removes the need
     * to perform a distracting <code>if (a == b) continue;</code> check on the
     * first line of the loop.
     * 
     * @param <T>
     * @param iterable
     * @param excluding
     * @return
     */
    public static <T> Iterable<T> excluding(Iterable<T> iterable, final T excluding) {
        return Iterables.filter(iterable, new Predicate<T>() {

            @Override
            public boolean apply(T input) {
                return !input.equals(excluding);
            }
        });
    }
}
