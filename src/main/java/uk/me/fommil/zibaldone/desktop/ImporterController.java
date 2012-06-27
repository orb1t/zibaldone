/*
 * Created 29-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import uk.me.fommil.zibaldone.Importer;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.NoteId;
import uk.me.fommil.zibaldone.Reconciler;

/**
 * Specialist MVC Controller for working with {@link Importer}s.
 * 
 * @author Samuel Halliday
 */
public class ImporterController {

    private static final Logger log = Logger.getLogger(ImporterController.class.getName());
    
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

    private final JungMainController main;

    private final Importer importer;

    private final UUID sourceId;

    /**
     * @param main
     * @param sourceId 
     */
    public ImporterController(JungMainController main, UUID sourceId) {
        Preconditions.checkNotNull(main);
        Preconditions.checkNotNull(sourceId);
        this.main = main;
        this.sourceId = sourceId;
        this.importer = main.getSettings().getImporters().get(sourceId);
    }

    public Importer getImporter() {
        return importer;
    }

    public void doImport() throws IOException {
        List<Note> notes = importer.getNotes();
        Reconciler reconciler = main.getReconciler();
        reconciler.reconcile(sourceId, notes);
        main.doRefresh();
    }

    public void doRemove() {
        main.getSettings().getImporters().remove(importer);
        // TODO: implement method
        log.warning("doRemove() not implemented yet");
    }
}
