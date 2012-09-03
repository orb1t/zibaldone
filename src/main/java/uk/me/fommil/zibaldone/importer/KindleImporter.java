/*
 * Created 08-Aug-2012
 * 
 * Copyright Hannu Rajaniemi 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.importer;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import uk.me.fommil.zibaldone.Exporter;
import uk.me.fommil.zibaldone.Importer;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Tag;

/**
 *
 * @author hannu
 */
@Log
public class KindleImporter implements Importer {

    public static class KindleSettings implements Importer.Settings {

        @Getter
        @Setter
        private File file = new File("/Volumes/Kindle/documents/My Clippings.txt");

    }

    @Getter
    private final KindleSettings settings = new KindleSettings();

    @Override
    public String getName() {

        return "Kindle";
    }

    @Override
    public List<Note> getNotes() throws IOException {

        if (!settings.getFile().exists()) {
            throw new IOException("File " + settings.getFile().getName() + " doesn't exist.");
        }
        // @Cleanup BufferedReader reader = Files.newReader(settings.getFile(), Charsets.UTF_8);
        String rawtext = Files.toString(settings.getFile(), Charsets.UTF_8);
        // Pattern pattern = Pattern.compile("", Pattern.MULTILINE);
        String[] entries = rawtext.split("==========");
        List<Note> notes = Lists.newArrayList();
        for (String entry : entries) {
            Note note = new Note();
            note.setContents(entry);
            StringReader entryreader = new StringReader(entry);
            @Cleanup BufferedReader bufferedReader = new BufferedReader(entryreader);
            String line = bufferedReader.readLine();
            Set<Tag> tags = Tag.asTags(line);
            note.setTags(tags);
            line = bufferedReader.readLine();
            line = bufferedReader.readLine();
            line = bufferedReader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                String title;
                if (line.length() >= 50) {
                    title = line.substring(0, 50);
                } else {
                    title = line;
                }
                note.setTitle(title);
                notes.add(note);
            }
        }
        return notes;
    }
}
