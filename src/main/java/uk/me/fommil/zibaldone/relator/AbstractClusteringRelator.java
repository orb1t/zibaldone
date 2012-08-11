/*
 * Created Aug 8, 2012
 * 
 * Copyright Hannu Rajaniemi 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.relator;

import java.util.Collection;
import java.util.Set;
import javax.persistence.EntityManagerFactory;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Relator;

/**
 * @author Hannu Rajaniemi
 */
public abstract class AbstractClusteringRelator implements Relator {

    @Override
    public void refresh(EntityManagerFactory emf) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Set<Note>> cluster(Collection<Note> notes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }    
}
