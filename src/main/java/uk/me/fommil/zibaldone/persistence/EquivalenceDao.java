/*
 * Created 12-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.persistence;

import java.util.List;
import javax.persistence.EntityManagerFactory;
import uk.me.fommil.persistence.CrudDao;
import uk.me.fommil.zibaldone.Equivalence;
import uk.me.fommil.zibaldone.Equivalence.Context;

/**
 * Data Access Object for {@link Equivalence} instances.
 * "AUTOMATIC_IGNORED" instances will exist side by side with their automatically
 * discovered instances. Convenience methods are provided to return sensible
 * lists of instances.
 * 
 * @author Samuel Halliday
 */
public class EquivalenceDao extends CrudDao<Long, Equivalence> {

    /**
     * @param emf
     */
    public EquivalenceDao(EntityManagerFactory emf) {
        super(Equivalence.class, emf);
    }

    /**
     * Resets the database store of automatic instances.
     * 
     * @param equivalences
     */
    public void updateAllAutomatics(List<Equivalence> equivalences) {
        if (count() > 0) {
            throw new UnsupportedOperationException("not implemented yet");
        }
        create(equivalences);
        
//        EntityManager em = createEntityManager();
//        Query clear = em.createQuery("DELETE " + getTableName() + " e WHERE e.context = :automatic");
//        clear.setParameter("automatic", Context.class.getName() + "." + Context.AUTOMATIC.name());
//        try {
//            em.getTransaction().begin();
//            clear.executeUpdate();
//            for (Equivalence equivalence : equivalences) {
//                Preconditions.checkArgument(equivalence.getContext() == Context.AUTOMATIC, "context was not AUTOMATIC");
//                Preconditions.checkArgument(equivalence.getStem() != null, "stem was null");
//                em.persist(equivalence);
//            }
//            em.getTransaction().commit();
//        } finally {
//            em.close();
//        }
    }

    /**
     * @param context if {@link Context#AUTOMATIC} is provided, then
     * entries which have a complimentary {@link Context#AUTOMATIC}
     * will not be returned.
     * @return
     */
    public List<Equivalence> read(Context context) {
        // TODO: implement method
        throw new UnsupportedOperationException("not implemented yet");
    }
}
