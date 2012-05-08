/*
 * Copyright Samuel Halliday 2011
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.persistence;

import uk.me.fommil.zibaldone.Note;
import javax.persistence.EntityManagerFactory;
import uk.me.fommil.persistence.CrudDao;

/**
 * DAO for {@link Note} instances.
 *
 * @author Samuel Halliday
 */
public class NoteDao extends CrudDao<Note, Long> {

        /**
         * @param emf
         */
        public NoteDao(EntityManagerFactory emf) {
                super(Note.class, emf);
        }
}
