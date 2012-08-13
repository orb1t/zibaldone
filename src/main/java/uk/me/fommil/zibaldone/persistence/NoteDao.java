/*
 * Copyright Samuel Halliday 2011
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.persistence;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.persistence.EntityManager;
import uk.me.fommil.zibaldone.Note;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import lombok.Cleanup;
import uk.me.fommil.persistence.CrudDao;
import uk.me.fommil.zibaldone.Bunch;
import uk.me.fommil.zibaldone.Tag;

/**
 * DAO for {@link Note} instances.
 *
 * @author Samuel Halliday
 */
public class NoteDao extends CrudDao<UUID, Note> {

    /**
     * @param emf
     */
    public NoteDao(EntityManagerFactory emf) {
        super(Note.class, emf);
    }

    /**
     * @param sourceId
     * @return
     */
    public long countForImporter(UUID sourceId) {
        Preconditions.checkNotNull(sourceId);
        @Cleanup("close") EntityManager em = createEntityManager();
        Query q = em.createQuery("SELECT COUNT(s) FROM " + getTableName() + " s WHERE source = :name");
        q.setParameter("name", sourceId);
        Long result = querySingle(em, q);
        return result;
    }

    /**
     * @return all tags
     */
    public Set<Tag> getAllTags() {
        @Cleanup("close") EntityManager em = createEntityManager();
        Query q = em.createQuery("SELECT DISTINCT t.text FROM " + getTableName() + " s JOIN s.tags t ORDER BY t.text");
        List<String> result = query(em, q);
        return Tag.asTags(result);
    }

    @Override
    protected void removeFromManyToManyMappings(EntityManager em, Note managedNote) {
//        Query q = em.createQuery("SELECT DISTINCT b FROM Bunch b INNER JOIN b.notes n WHERE n.id = :id");
//        q.setParameter("id", managedNote.getId());
//        @SuppressWarnings("unchecked")
//        Collection<Bunch> bunches = q.getResultList();
        Collection<Bunch> bunches = managedNote.getBunches();
        for (Bunch bunch : bunches) {
            bunch.getNotes().remove(managedNote);
            em.merge(bunch);
        }
    }

    public List<Note> readForImporter(UUID sourceId) {
        Preconditions.checkNotNull(sourceId);
        @Cleanup("close") EntityManager em = createEntityManager();
        Query q = em.createQuery("SELECT s FROM " + getTableName() + " s WHERE source = :sourceId");
        q.setParameter("sourceId", sourceId);
        return query(em, q);
    }
}
