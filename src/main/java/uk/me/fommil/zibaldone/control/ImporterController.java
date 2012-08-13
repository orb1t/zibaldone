/*
 * Created 29-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.control;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.UUID;
import javax.persistence.EntityManagerFactory;
import lombok.ListenerSupport;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import uk.me.fommil.zibaldone.Importer;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Reconciler;
import uk.me.fommil.zibaldone.Tag;
import uk.me.fommil.zibaldone.control.Listeners.NoteListener;
import uk.me.fommil.zibaldone.control.Listeners.TagListener;
import uk.me.fommil.zibaldone.control.TagController.TagChoice;
import uk.me.fommil.zibaldone.persistence.NoteDao;

/**
 * Specialist MVC Controller for working with {@link Importer}s.
 * 
 * @author Samuel Halliday
 */
@Log
@RequiredArgsConstructor
@ListenerSupport({TagListener.class, NoteListener.class})
public class ImporterController {

    /**
     * @param klass
     * @return indexed by the proposed {@link NoteId#setSource(String)}
     */
    public Map.Entry<UUID, Importer> newImporter(Class<Importer> klass) {
        Preconditions.checkNotNull(klass);

        final Importer importer;
        try {
            importer = klass.newInstance();
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to load Importer: " + klass.getName(), ex);
        }
        UUID uuid = UUID.randomUUID();
        return new AbstractMap.SimpleImmutableEntry<UUID, Importer>(uuid, importer);
    }

    /**
     * @return the {@link Importer} implementations, indexed by their name.
     */
    public Map<String, Class<Importer>> getImporterImplementations() {
        ServiceLoader<Importer> importerService = ServiceLoader.load(Importer.class);
        HashMap<String, Class<Importer>> importerImpls = Maps.newHashMap();
        for (Importer importer : importerService) {
            String name = importer.getName();
            @SuppressWarnings("unchecked")
            Class<Importer> klass = (Class<Importer>) importer.getClass();
            importerImpls.put(name, klass);
        }
        return importerImpls;
    }

    /**
     * Convenience to create a new Importer instance by its user "name".
     *
     * @param name
     * @return
     */
    public Map.Entry<UUID, Importer> newImporter(String name) {
        return newImporter(getImporterImplementations().get(name));
    }

    @NonNull
    private final EntityManagerFactory emf;

    @NonNull
    private final Settings settings;

    public void loadDb() {
        NoteDao dao = new NoteDao(emf);

        Set<Tag> tags = dao.getAllTags();
        Set<Note> notes = Sets.newHashSet(dao.readAll());

        fireNotesChanged(notes);
        fireTagsAdded(tags);
        for (Entry<Tag, TagChoice> entry : settings.getSelectedTags().entrySet()) {
            fireTagSelection(entry.getKey(), entry.getValue());
        }
    }

    public Importer getImporter(UUID sourceId) {
        return settings.getImporters().get(sourceId);
    }

    public void doImport(UUID sourceId) throws IOException {
        NoteDao dao = new NoteDao(emf);

        Set<Tag> tagsBefore = dao.getAllTags();

        List<Note> importedNotes = getImporter(sourceId).getNotes();
        Reconciler reconciler = new Reconciler(emf);
        reconciler.reconcile(sourceId, importedNotes, Reconciler.SIMPLE_RECONCILE);

        Set<Tag> tagsAfter = dao.getAllTags();
        diffTags(tagsBefore, tagsAfter);

        Set<Note> notes = Sets.newHashSet(dao.readAll());
        fireNotesChanged(notes);
    }

    public void doRemove(UUID sourceId) {
        Preconditions.checkNotNull(sourceId);
        settings.getImporters().remove(sourceId);
        NoteDao dao = new NoteDao(emf);
        List<Note> toRemove = dao.readForImporter(sourceId);
        dao.delete(toRemove);
        Set<Note> notes = Sets.newHashSet(dao.readAll());
        fireNotesChanged(notes);
    }

    private void diffTags(Set<Tag> before, Set<Tag> after) {
        Set<Tag> removed = Sets.newHashSet();
        for (Tag note : before) {
            if (!after.contains(note)) {
                removed.add(note);
            }
        }
        if (!removed.isEmpty()) {
            fireTagsRemoved(removed);
        }

        Set<Tag> added = Sets.newHashSet();
        for (Tag note : after) {
            if (!before.contains(note)) {
                added.add(note);
            }
        }
        if (!added.isEmpty()) {
            fireTagsAdded(added);
        }
    }
}
