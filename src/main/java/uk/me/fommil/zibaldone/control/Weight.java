/*
 * Created 02-Jul-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.control;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.commons.collections15.Transformer;

/**
 * Workaround for inadequacy in JUNG where edges are expected to be
 * unique: an epic fail for a simple weighted graph!
 * 
 * @author Samuel Halliday
 */
@RequiredArgsConstructor
@ToString
public class Weight implements Comparable<Weight> {

    /** Required for some JUNG visualisers to understand the meaning of the edge */
    public static final Transformer<Weight, Integer> TRANSFORMER = new Transformer<Weight, Integer>() {
        @Override
        public Integer transform(Weight input) {
            Preconditions.checkNotNull(input);
            return Math.round((float) (input.getWeight() * 100.0));
        }
    };

    @Getter
    private final double weight;

    @Override
    public int compareTo(Weight o) {
        return Double.compare(weight, o.weight);
    }
}
