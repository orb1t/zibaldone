/*
 * Created 10-Jul-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.persistence;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import lombok.Cleanup;
import uk.me.fommil.persistence.CrudDao;
import uk.me.fommil.zibaldone.Bunch;

/**
 * DAO for {@link Bunch} instances.
 *
 * @author Samuel Halliday
 */
public class BunchDao extends CrudDao<Long, Bunch> {

    public BunchDao(EntityManagerFactory emf) {
        super(Bunch.class, emf);
    }

    // Fix for Hibernate BUG: http://stackoverflow.com/questions/11604370
    @Override
    public Bunch read(Long key) {
        Preconditions.checkNotNull(key);
        @Cleanup("close") EntityManager em = createEntityManager();
        TypedQuery<Bunch> q = em.createQuery("SELECT b FROM Bunch b WHERE b.id = :id", Bunch.class);
        q.setParameter("id", key);
        return querySingle(em, q);
    }

    // Fix for Hibernate BUG: http://stackoverflow.com/questions/11604370
    @Override
    public List<Bunch> read(Collection<Long> keys) {
        Preconditions.checkNotNull(keys);
        List<Bunch> bunches = Lists.newArrayList();
        for (Long key : keys) {
            Bunch bunch = read(key);
            bunches.add(bunch);
        }
        return bunches;
    }
}
