/*
 * Created 08-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

/**
 * Provides a metric between {@link Note} instances to be used in user interfaces
 * and machine learning algorithms.
 * 
 * @author Samuel Halliday
 */
public interface Relator {

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
}
