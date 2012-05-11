/*
 * Copyright Samuel Halliday 2011
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.importer;

import uk.me.fommil.zibaldone.Note;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import javax.persistence.EntityManagerFactory;
import uk.me.fommil.persistence.CrudDao;
import uk.me.fommil.zibaldone.Tag;
import uk.me.fommil.zibaldone.persistence.NoteDao;

/**
 * Parse Emacs <a href="http://orgmode.org/">Org-Mode</a> files.
 *
 * @author Samuel Halliday
 */
public class OrgModeParser {

        private static final Pattern startPattern = Pattern.compile("^\\*+\\s");

        private static final Logger log = Logger.getLogger(OrgModeParser.class.getName());

        private final File file;

        /**
         * @param args
         * @throws Exception
         */
        public static void main(String[] args) throws Exception {
                EntityManagerFactory emf = CrudDao.createEntityManagerFactory("ZibaldonePU");

                OrgModeParser parser = new OrgModeParser(new File("../data/QT2-notes.org"));
                List<Note> notes = parser.parseAll();


                try {
                        NoteDao dao = new NoteDao(emf);

                        long count = dao.count();
                        System.out.println("counting " + count);

                        dao.create(notes);

                        count = dao.count();
                        System.out.println("counting " + count);
                } finally {
                        emf.close();
                }
        }

        /**
         * @param file
         */
        public OrgModeParser(File file) {
                Preconditions.checkNotNull(file);
                Preconditions.checkArgument(file.isFile(), file);
                this.file = file;
        }

        /**
         * @return
         * @throws IOException
         */
        public List<Note> parseAll() throws IOException {
                BufferedReader reader = Files.newReader(file, Charset.defaultCharset());
                try {
                        List<Note> notes = Lists.newArrayList();
                        StringBuilder rawNote = new StringBuilder();
                        Map<Integer, Note> hierarchy = Maps.newTreeMap();
                        String line;
                        while ((line = reader.readLine()) != null) {
                                if (startPattern.matcher(line).find()) {
                                        Entry<Integer, Note> note = parseNote(rawNote.toString(), hierarchy);
                                        if (!note.getValue().getTitle().isEmpty()) {
                                                notes.add(note.getValue());
                                                log.info(note.toString());
                                                hierarchy.put(note.getKey(), note.getValue());
                                        }
                                        rawNote = new StringBuilder();
                                }
                                rawNote.append(line);
                                rawNote.append("\n");
                        }
                        return notes;
                } finally {
                        Closeables.closeQuietly(reader);
                }
        }

        // integer key represents the depth of the note
        private Entry<Integer, Note> parseNote(String rawNote, Map<Integer, Note> hierarchy) throws IOException {
                BufferedReader reader = new BufferedReader(new StringReader(rawNote));
                String header = reader.readLine();

                Integer depth = header.indexOf(" ");

                List<String> headerParts = Arrays.asList(header.split(":"));
                String title = headerParts.get(0).replace("*", "").trim();
                Iterable<String> strings = Iterables.skip(headerParts, 1);
                List<Tag> tags = Lists.newArrayList(Tag.asTags(strings));
                if (hierarchy.containsKey(depth - 1)) {
                        tags.addAll(hierarchy.get(depth - 1).getTags());
                }

                String contents = new String(rawNote.substring(header.length() + 1));

                Note note = new Note();
                note.setTitle(title);
                note.setTags(tags);
                note.setContents(contents);

                return Maps.immutableEntry(depth, note);
        }
}
