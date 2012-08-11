/*
 * Created 08-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

import java.beans.XMLDecoder;
import java.util.Collection;
import java.util.Set;
import javax.persistence.EntityManagerFactory;

/**
 * Provides a metric and
 * <a href="http://en.wikipedia.org/wiki/Cluster_analysis">cluster analysis</a>
 * between {@link Note}s.
 * <p>
 * Implementations must ensure backwards compatibility of their
 * {@link XMLDecoder} form (i.e. an instance must be able to be created using
 * an XML save state from an earlier version) and have a no-arguments
 * public constructor.
 * 
 * @author Samuel Halliday
 */
public interface Relator {

    /**
     * Empty interface indicating a Relator's settings Javabeans object.
     */
    public interface Settings {
    }

    /**
     * @return implementation-dependent user settings
     */
    public Settings getSettings();

    /**
     * @return a user-friendly name for this implementation.
     */
    public String getName();

    /**
     * Called after XML decoding or prior to (repeated) calls of
     * {@link #relate(Note, Note)} and {@link #cluster(Collection)}.
     *
     * @param emf
     */
    public void refresh(EntityManagerFactory emf);

    /**
     * Defines a metric (i.e. symmetric, positive, etc) between the parameters
     * which is {@code 0.0} if the objects are equal. Implementations are free
     * to decide how the metric is calculated - which need not be deterministic
     * - but the returned value must be in the domain {@code [0, 1]}.
     * 
     * @param a
     * @param b
     * @return 
     */
    public double relate(Note a, Note b);

    /**
     * Clusters (in the machine learning sense) {@link Note}s into distinct sets.
     *
     * @param notes
     * @return
     */
    public Set<Set<Note>> cluster(Collection<Note> notes);
}
