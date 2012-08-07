/*
 * Created 12-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.persistence;

import com.google.common.base.Preconditions;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import lombok.Cleanup;
import lombok.extern.java.Log;
import uk.me.fommil.persistence.CrudDao;
import uk.me.fommil.zibaldone.Synonym;
import uk.me.fommil.zibaldone.Synonym.Context;

/**
 * Data Access Object for {@link Synonym} instances.
 * "AUTOMATIC_IGNORED" instances will exist side by side with their automatically
 * discovered instances. Convenience methods are provided to return sensible
 * lists of instances.
 * 
 * @author Samuel Halliday
 */
@Log
public class SynonymDao extends CrudDao<Long, Synonym> {

    /**
     * @param emf
     */
    public SynonymDao(EntityManagerFactory emf) {
        super(Synonym.class, emf);
    }

    /**
     * Resets the database store of automatic instances.
     * 
     * @param synonyms
     */
    public void updateAllAutomatics(List<Synonym> synonyms) {
        @Cleanup("close") EntityManager em = createEntityManager();

        em.getTransaction().begin();

        // ??: would be faster with a query, but this fails with broken constraints
//        Query q = em.createQuery("DELETE FROM " + getTableName() + " s WHERE s.context = :context");
//        q.setParameter("context", Synonym.Context.AUTOMATIC);
//        q.executeUpdate();

        Query q = em.createQuery("SELECT s FROM " + getTableName() + " s WHERE s.context = :context");
        q.setParameter("context", Synonym.Context.AUTOMATIC);
        @SuppressWarnings("unchecked")
        List<Synonym> results = q.getResultList();
        if (!results.isEmpty()) {
            for (Synonym synonym : results) {
                em.remove(synonym);
            }
        }
        for (Synonym synonym : synonyms) {
            Preconditions.checkArgument(synonym.getContext() == Context.AUTOMATIC, "context was not AUTOMATIC");
            Preconditions.checkArgument(synonym.getStem() != null, "stem was null");
            em.persist(synonym);
        }
        em.getTransaction().commit();
    }
}
