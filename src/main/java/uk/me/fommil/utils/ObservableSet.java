/*
 * Created 03-Aug-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.utils;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Set;

/**
 * Compliments {@link ObservableCollection} with a wrapper to indicate
 * that the underlying {@link Collection} is a {@link Set}. Provides no
 * additional functionality.
 * 
 * @param <T> 
 * @author Samuel Halliday
 */
public class ObservableSet<T> extends ObservableCollection<T> implements Set<T> {

    /**
     * @param <T>
     * @param collection
     * @return 
     */
    public static <T> ObservableSet<T> observable(Set<T> collection) {
        Preconditions.checkNotNull(collection);
        return new ObservableSet<T>(collection);
    }

    public ObservableSet(Set<T> set) {
        super(set);
    }
}
