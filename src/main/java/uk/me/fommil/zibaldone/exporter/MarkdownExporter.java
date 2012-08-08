/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.me.fommil.zibaldone.exporter;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import uk.me.fommil.zibaldone.Bunch;
import uk.me.fommil.zibaldone.Exporter;
import uk.me.fommil.zibaldone.Note;

/**
 *
 * @author hannu
 */
public class MarkdownExporter implements Exporter {

    public static class MdownSettings implements Settings {

        @Getter
        @Setter
        private File file = new File("zibaldone.mdown"); 
    }
    private final MdownSettings settings = new MdownSettings();

    @Override
    public MdownSettings getSettings() {
        return settings;
    }

    @Override
    public String getName() {
        return "Markdown";
    }

    @Override
    public void export(Collection<Bunch> bunches) throws IOException {
        @Cleanup
        Writer writer = Files.newWriter(settings.getFile(), Charsets.UTF_8);
        for (Bunch bunch : bunches) {
            writer.write("# " + bunch.getName() + " #\n\n");
            writer.write(bunch.getContents() + "\n\n");
            for (Note note : bunch.getNotes()) {
                writer.write("## " + note.getTitle() + " ##\n\n");
                writer.write(note.getContents() + "\n\n"); 
            }
        }

    }
}
