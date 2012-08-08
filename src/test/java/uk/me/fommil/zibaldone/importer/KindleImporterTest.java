/*
 * Created Aug 8, 2012
 * 
 * Copyright Hannu Rajaniemi 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.importer;

import java.io.File;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.me.fommil.zibaldone.Note;

/**
 *
 * @author Hannu Rajaniemi
 */
public class KindleImporterTest {
    
    public KindleImporterTest() {
    }

    @Test
    public void testGetSettings() {
    }

    @Test
    public void testGetName() {
    }

    @Test
    public void testGetNotes() throws Exception {
        KindleImporter importer = new KindleImporter(); 
        importer.getSettings().setFile(new File("../data/My Clippings.txt"));
        List<Note> notes = importer.getNotes(); 
        assertTrue(notes.size()>10);
    }
}
