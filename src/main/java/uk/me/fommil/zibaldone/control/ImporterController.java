/*
 * Created 29-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.control;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.UUID;
import lombok.ListenerSupport;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import uk.me.fommil.zibaldone.Importer;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Reconciler;
import uk.me.fommil.zibaldone.Tag;
import uk.me.fommil.zibaldone.control.Listeners.TagsChangedListener;
import uk.me.fommil.zibaldone.persistence.NoteDao;

/**
 * Specialist MVC Controller for working with {@link Importer}s.
 * 
 * @author Samuel Halliday
 */
@Log
@RequiredArgsConstructor
@ListenerSupport({TagsChangedListener.class})
public class ImporterController {

    /**
     * @param klass
     * @return indexed by the proposed {@link NoteId#setSource(String)}
     */
    public static Map.Entry<UUID, Importer> newImporter(Class<Importer> klass) {
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
    public static Map<String, Class<Importer>> getImporterImplementations() {
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
    public static Map.Entry<UUID, Importer> newImporter(String name) {
        return newImporter(getImporterImplementations().get(name));
    }

    @NonNull
    private final JungMainController main;

    @NonNull
    private final UUID sourceId;

    public Importer getImporter() {
        return main.getSettings().getImporters().get(sourceId);
    }

    public void doImport() throws IOException {
        List<Note> notes = getImporter().getNotes();
        Reconciler reconciler = new Reconciler(main.getEmf());
        reconciler.reconcile(sourceId, notes);
        NoteDao noteDao = new NoteDao(main.getEmf());
        Set<Tag> tags = noteDao.getAllTags();
        
        log.info("TAGS: " + tags.size());
        
        fireTagsChanged(tags);
        main.doRefresh();
    }

    public void doRemove() {
        main.getSettings().getImporters().remove(sourceId);
        // TODO: implement method
        log.warning("doRemove() not implemented yet");
    }
}
