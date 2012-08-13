/*
 * Created 13-Aug-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.persistence;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.persistence.EntityManagerFactory;
import org.junit.Assert;
import org.junit.Test;
import uk.me.fommil.persistence.CrudDao;
import uk.me.fommil.zibaldone.Bunch;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Tag;

/**
 *
 * @author Samuel Halliday
 */
public class BunchDaoTest {

    @Test
    public void testDelete() {
        EntityManagerFactory emf = CrudDao.createEntityManagerFactory("ZibaldoneTestPU");


        Note a = new Note();
        a.setTitle("a");
        a.setTags(Tag.asTags("smug", "silly"));

        Note b = new Note();
        b.setTitle("b");
        b.setTags(Tag.asTags("silly", "sausage"));

        Note c = new Note();
        c.setTitle("c");
        c.setTags(Tag.asTags("sausage", "chips"));

        Set<Note> notes = Sets.newHashSet(a, b, c);

        NoteDao noteDao = new NoteDao(emf);
        long before = noteDao.count();

        noteDao.create(notes);

        Bunch bunch = new Bunch();
        bunch.setName("Bunch");
        bunch.setNotes(notes);

        BunchDao bunchDao = new BunchDao(emf);
        bunchDao.create(bunch);
        bunchDao.delete(bunch);

        long after = noteDao.count();
        Assert.assertEquals(before + 3, after);
    }
}
