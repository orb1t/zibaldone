/*
 * Created 25-Jun-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.utils;

import com.google.common.base.Preconditions;
import java.util.List;

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
}
