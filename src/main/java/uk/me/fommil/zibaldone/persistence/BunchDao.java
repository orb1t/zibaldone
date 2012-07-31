/*
 * Created 10-Jul-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.persistence;

import java.util.UUID;
import javax.persistence.EntityManagerFactory;
import uk.me.fommil.persistence.CrudDao;
import uk.me.fommil.zibaldone.Bunch;

/**
 * DAO for {@link Bunch} instances.
 *
 * @author Samuel Halliday
 */
public class BunchDao extends CrudDao<UUID, Bunch> {

    public BunchDao(EntityManagerFactory emf) {
        super(Bunch.class, emf);
    }
}
