/*
 * Copyright Samuel Halliday 2011
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.persistence;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import uk.me.fommil.zibaldone.Note;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import uk.me.fommil.persistence.CrudDao;
import uk.me.fommil.zibaldone.Tag;

/**
 * DAO for {@link Note} instances.
 *
 * @author Samuel Halliday
 */
public class NoteDao extends CrudDao<Long, Note> {
    
    private static final Logger log = Logger.getLogger(NoteDao.class.getName());

    /**
     * @param emf
     */
    public NoteDao(EntityManagerFactory emf) {
        super(Note.class, emf);
    }

    /**
     * @param importerName
     * @return
     */
    public long countForImporter(String importerName) {
        Preconditions.checkNotNull(importerName);
        EntityManager em = createEntityManager();
        Query q = em.createQuery("SELECT COUNT(s) FROM " + getTableName() + " s WHERE id.source = :name");
        q.setParameter("name", importerName);
        Long result = querySingle(em, q);
        return result;
    }

    /**
     * @return a list of all tags.
     */
    public List<Tag> getAllTags() {
        EntityManager em = createEntityManager();
        Query q = em.createQuery("SELECT DISTINCT t.text FROM " + getTableName() + " s JOIN s.tags t");
        List<String> result = query(em, q);
        return Tag.asTags(result);
    }
}
