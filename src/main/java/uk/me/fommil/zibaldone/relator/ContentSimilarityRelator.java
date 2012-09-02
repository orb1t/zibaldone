/*
 * Created 02-Sep-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.relator;

import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Relator;

/**
 * {@link Relator} which uses NLP to define a metric of similarity between
 * {@link Note}s.
 *
 * @author Samuel Halliday
 */
public class ContentSimilarityRelator extends AbstractClusteringRelator {

    @Override
    public String getName() {
        return "Similarity";
    }

    @Override
    public double relate(Note a, Note b) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
