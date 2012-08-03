/*
 * Created 03-Aug-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.concurrent.NotThreadSafe;
import lombok.Delegate;
import lombok.Getter;
import lombok.ListenerSupport;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Wrapper that allows changes to a {@link Collection} to be observed. This is
 * an internally written class because Guava opted not to implement this
 * functionality.
 * <p>
 * Not able to detect changes to mutable elements. May not send events if
 * an operation was attempted which did not change the underlying
 * {@link Collection}.
 * <p>
 * Actions on the underlying {@link Collection} will obviously no longer be
 * atomic, which may affect the thread safety of their use.
 * This will otherwise not affect the thread safety of the delegate, but
 * makes no guarantees about the thread safety of the observations,
 * i.e. observations may arrive out of
 * sync with the actual order in which they were performed and may already be
 * out of date by the time they are received.
 * <p>
 * Listeners should be mindful of what they intend to do with observations, as
 * changes to the underlying {@link Collection} may result in unexpected
 * behaviour.
 *
 * @param <T> 
 * @author Samuel Halliday
 * @see <a href="http://code.google.com/p/guava-libraries/issues/detail?id=1077">Guava RFE</a>
 * @see <a href="http://docs.oracle.com/javafx/2/api/javafx/collections/package-frame.html">JavaFX Collections</a>
 */
@RequiredArgsConstructor
@NotThreadSafe
@ListenerSupport(ObservableCollection.CollectionListener.class)
public class ObservableCollection<T> implements Collection<T> {

    /**
     * @param <T>
     * @param collection
     * @return 
     */
    public static <T> ObservableCollection<T> observable(Collection<T> collection) {
        Preconditions.checkNotNull(collection);
        return new ObservableCollection<T>(collection);
    }

    @RequiredArgsConstructor
    @Getter
    public static final class Change<T> {

        @NonNull
        private final ObservableCollection<T> collection;

        @NonNull
        private final Collection<? extends T> elementsAdded;

        @NonNull
        private final Collection<?> elementsRemoved;

        @Accessors(fluent = true)
        private final boolean wasAdded, wasRemoved;

    }

    /**
     * Listen to changes in {@link ObservableCollection}s.
     *
     * @param <T> 
     */
    public interface CollectionListener<T> {

        /**
         * Called after a change has been made to an {@link ObservableCollection}.
         * Note that in bulk updates, all elements which were requested to be
         * added or removed will be included, but this does not guarantee that the
         * elements were present (or not present) before the operation.
         * 
         * @param change
         */
        public void onCollectionChanged(Change<T> change);
    }

    // subset of the Collection API which results in changes
    private interface Mutators<T> {

        public Iterator<T> iterator();

        public boolean add(T e);

        public boolean remove(Object o);

        public boolean addAll(Collection<? extends T> c);

        public boolean removeAll(Collection<?> c);

        public boolean retainAll(Collection<?> c);

        public void clear();
    }

    @Delegate(excludes = Mutators.class)
    private final Collection<T> delegate;

    private Change<T> createAdditionChange(T added) {
        return createAdditionChange(Collections.singleton(added));
    }

    private Change<T> createAdditionChange(Collection<? extends T> added) {
        Change<T> change = new Change<T>(this, added, Collections.<T>emptySet(), true, false);
        return change;
    }

    private Change<T> createRemovalChange(Object removed) {
        return createRemovalChange(Collections.singleton(removed));
    }

    private Change<T> createRemovalChange(Collection<?> removed) {
        return new Change<T>(this, Collections.<T>emptySet(), removed, false, true);
    }

    // HERE DOWN IS THE IMPLEMENTATION
    @Override
    public Iterator<T> iterator() {
        final Iterator<T> iterator = delegate.iterator();
        return new Iterator<T>() {
            private volatile T current;

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                current = iterator.next();
                return current;
            }

            @Override
            public void remove() {
                iterator.remove();
                fireOnCollectionChanged(createRemovalChange(current));
            }
        };
    }

    @Override
    public boolean add(T e) {
        if (delegate.add(e)) {
            fireOnCollectionChanged(createAdditionChange(e));
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        if (delegate.remove(o)) {
            fireOnCollectionChanged(createRemovalChange(o));
            return true;
        }
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        if (delegate.addAll(c)) {
            fireOnCollectionChanged(createAdditionChange(c));
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (delegate.removeAll(c)) {
            fireOnCollectionChanged(createRemovalChange(c));
            return true;
        }
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        List<T> before = Lists.newArrayList(delegate);
        if (delegate.retainAll(c)) {
            List<T> lost = Lists.newArrayList();
            for (T old : before) {
                if (!c.contains(old)) {
                    lost.add(old);
                }
            }
            fireOnCollectionChanged(createRemovalChange(lost));
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        List<T> before = Lists.newArrayList(delegate);
        delegate.clear();
        if (!before.isEmpty()) {
            fireOnCollectionChanged(createRemovalChange(before));
        }
    }
}
