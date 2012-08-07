/*
 * Created 07-Aug-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

import java.io.File;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManagerFactory;
import org.junit.Assert;
import org.junit.Test;
import uk.me.fommil.persistence.CrudDao;
import uk.me.fommil.zibaldone.importer.OrgModeImporter;
import uk.me.fommil.zibaldone.persistence.NoteDao;

/**
 *
 * @author Samuel Halliday
 */
public class ReconcilerTest {

    @Test
    public void testReconcile() throws Exception {
        EntityManagerFactory emf = CrudDao.createEntityManagerFactory("ZibaldoneTestPU");
        File file1 = new File("../data/QT2-notes-1.org");
        File file2 = new File("../data/QT2-notes-2.org");

        OrgModeImporter importer = new OrgModeImporter();
        importer.getSettings().setFile(file1);
        List<Note> notes1 = importer.getNotes();

        importer.getSettings().setFile(file2);
        List<Note> notes2 = importer.getNotes();

        Reconciler reconciler = new Reconciler(emf);
        UUID uuid = UUID.nameUUIDFromBytes("ReconcilerTest".getBytes());
        reconciler.reconcile(uuid, notes1);

        NoteDao noteDao = new NoteDao(emf);
        long count = noteDao.count();

        reconciler.reconcile(uuid, notes2);

        Assert.assertEquals(count, noteDao.count());
    }
}
