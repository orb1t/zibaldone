/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.me.fommil.zibaldone.exporter;

import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.me.fommil.zibaldone.Exporter.Settings;
import uk.me.fommil.zibaldone.exporter.MarkdownExporter.MdownSettings;

/**
 *
 * @author hannu
 */
public class MarkdownExporterTest {
    
    @Test
    public void testGetSettings() {
        
        MarkdownExporter exporter = new MarkdownExporter();
        MdownSettings settings = exporter.getSettings(); 
        File testfile = new File("testfile.mdown"); 
        settings.setFile(testfile);
        assertEquals(testfile, settings.getFile());
    }

    @Test
    public void testGetName() {
                   }

    @Test
    public void testExport() throws Exception {
    }
}
