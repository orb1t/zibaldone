/*
 * Created 03-Sep-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.importer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import uk.me.fommil.zibaldone.Importer;
import uk.me.fommil.zibaldone.Note;

/**
 * Imports <a href="http://evernote.com">Evernote</a> archives.
 *
 * @author Samuel Halliday
 */
public class LocalEvernoteImporter implements Importer {

    public static class LocalEvernoteSettings implements Importer.Settings {

        @Getter
        @Setter
        private File file;

    }

    @Getter
    private final LocalEvernoteSettings settings = new LocalEvernoteSettings();

    @Override
    public String getName() {
        return "Evernote Archive";
    }

    @Override
    public List<Note> getNotes() throws IOException {
        throw new UnsupportedOperationException("LocalEvernoteImporter not supported yet.");
    }
}
