/*
 * Created 08-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

import java.io.IOException;
import java.util.*;

/**
 * API for importing {@link Note} objects from a variety of file formats and
 * sources.
 * <p>
 * All implementations must have a default public constructor.
 * 
 * @author Samuel Halliday
 */
public interface Importer {
    
    /**
     * Empty interface indicating an Importer's settings Javabeans object.
     */
    public interface Settings {
        
    }

    /**
     * @return a name which identifies the instance name. It should
     * remain the same across sessions which have the same <i>special</i>
     * properties.
     * 
     * The returned string must not be longer than 24 characters.
     * @see #getSpecialProperties()
     */
    public String getInstanceName();

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

    /**
     * A JavaBean with properties that may influence the identity of the instance.
     * 
     * @return a JavaBean
     * @see #setSettings(Object)
     */
    public Settings getSettings();

    /**
     * @param settings
     * @see #getSettings()
     */
    public void setSettings(Settings settings);

    
    /**
     * The names of the JavaBean properties that uniquely identify the instance
     * across sessions, e.g. filenames, usernames, URLs.
     * 
     * @return list of JavaBean property names
     */
    public List<String> getSpecialPropertyNames();
}
