/*
 * Created 29-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import uk.me.fommil.zibaldone.Importer;
import uk.me.fommil.zibaldone.Importer.Settings;
import uk.me.fommil.zibaldone.Note;
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
     * @param settings {@code null} for defaults
     * @return
     */
    public static ImporterController forClass(
            Class<Importer> klass, @Nullable Settings settings) {
        Preconditions.checkNotNull(klass);

        final Importer importer;
        try {
            importer = klass.newInstance();
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to load Importer: " + klass.getName(), ex);
        }
        if (settings != null) {
            importer.setSettings(settings);
        }

        return new ImporterController(importer);
    }

    private final Importer importer;

    /**
     * @param importer
     */
    public ImporterController(Importer importer) {
        this.importer = importer;
    }

    public Importer getImporter() {
        return importer;
    }

    public boolean isSpecial(String propertyName) {
        return importer.getSpecialPropertyNames().contains(propertyName);
    }

    public void doImport() throws IOException {
        List<Note> notes = importer.getNotes();

        Reconciler reconciler = new Reconciler();
        reconciler.reconcile(importer, notes);
    }

    public void doRemove() {
        // TODO: implement method
        log.warning("doRemove() not implemented yet");
    }
}
