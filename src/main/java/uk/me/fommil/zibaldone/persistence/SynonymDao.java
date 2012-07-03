/*
 * Created 12-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.persistence;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import lombok.extern.java.Log;
import uk.me.fommil.persistence.CrudDao;
import uk.me.fommil.zibaldone.Synonym;
import uk.me.fommil.zibaldone.Synonym.Context;
import uk.me.fommil.zibaldone.Tag;

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
        if (count() > 0) {
            throw new UnsupportedOperationException("not implemented yet");
        }
        create(synonyms);

//        @Cleanup("close") EntityManager em = createEntityManager();
//        Query clear = em.createQuery("DELETE " + getTableName() + " e WHERE e.context = :automatic");
//        clear.setParameter("automatic", Context.class.getName() + "." + Context.AUTOMATIC.name());
//            em.getTransaction().begin();
//            clear.executeUpdate();
//            for (Synonym equivalence : equivalences) {
//                Preconditions.checkArgument(equivalence.getContext() == Context.AUTOMATIC, "context was not AUTOMATIC");
//                Preconditions.checkArgument(equivalence.getStem() != null, "stem was null");
//                em.persist(equivalence);
//            }
//            em.getTransaction().commit();
    }

    /**
     * @return all the synonyms minus the {@link Context#AUTOMATIC} instances
     * that have a complimentary {@link Context#AUTOMATIC_IGNORED} instance.
     */
    public ListMultimap<Synonym.Context, Synonym> readActive() {
        log.info("Not Implemented Yet");
        return ArrayListMultimap.create();
    }
        
    /**
     * @param tags
     * @return the synonyms that have a non-zero intersection of tags with the input.
     */
    public List<Synonym> readByTags(List<Tag> tags) {
        Preconditions.checkNotNull(tags);
        Preconditions.checkArgument(!tags.isEmpty());
        
        // TODO: implement method
        throw new UnsupportedOperationException("not implemented yet");
    }
}
