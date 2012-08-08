/*
 * Created Aug 8, 2012
 * 
 * Copyright Hannu Rajaniemi 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.relator;

import uk.me.fommil.zibaldone.Note;

/**
 *
 * @author Hannu Rajaniemi
 */
public class RandomRelator extends AbstractClusteringRelator {

    @Override
    public Settings getSettings() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getName() {
        return "Random";
    }

    @Override
    public double relate(Note a, Note b) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
