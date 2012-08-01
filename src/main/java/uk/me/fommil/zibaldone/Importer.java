/*
 * Created 08-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

import java.beans.XMLDecoder;
import java.io.IOException;
import java.util.*;

/**
 * API for importing {@link Note} objects from a variety of file formats and
 * sources.
 * <p>
 * Implementations must ensure backwards compatibility of their
 * {@link XMLDecoder} form (i.e. an instance must be able to be created using
 * an XML save state from an earlier version) and have a no-arguments
 * public constructor.
 * 
 * @author Samuel Halliday
 */
public interface Importer {

    // TODO: Delicious Importer
    // TODO: EverNote Importer
    // TODO: Apple Stickies Importer
    // TODO: Apple Notes Importer
    
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
     * @return all the {@link Note}s from the input channel.
     * Implementations should not cache this result as the caller expects
     * a fresh reload of the underlying resource.
     * {@code id} values will be ignored.
     * 
     * @throws IOException
     */
    public List<Note> getNotes() throws IOException;
}
