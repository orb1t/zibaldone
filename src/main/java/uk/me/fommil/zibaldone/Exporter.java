/*
 * Created 08-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

import java.io.IOException;
import java.util.Collection;

/**
 * API for exporting {@link Bunch} objects (and their contents) to a variety of
 * file formats and locations.
 *
 * @author Samuel Halliday
 */
public interface Exporter {

    /**
     * Empty interface indicating an Importer's settings Javabeans object.
     */
    public interface Settings {
    }

    /**
     * @return implementation-dependent user settings
     */
    public Settings getSettings();

    /**
     * @return a user-friendly name for this implementation.
     */
    public String getName();

    /**
     * @throws IOException
     */
    public void export(Collection<Bunch> bunches) throws IOException;
}
