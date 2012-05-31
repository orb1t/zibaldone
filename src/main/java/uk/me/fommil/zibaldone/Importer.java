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
 * TODO: handle cases where the underlying resources are moved - all the instance
 * names of an Importer could be changed to reflect the new importer.
 * 
 * @author Samuel Halliday
 */
public interface Importer {

    /**
     * @return a name which identifies the instance name. It should
     * remain the same across sessions which have the same <i>special</i>
     * properties.
     * 
     * The returned string must not be longer than 24 characters.
     * @see #getSpecialPropertyNames()
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
     * Transient properties which do not have any influence on the persistent
     * state of the instance. e.g. passwords, filter details.
     * <p>
     * Must not share any names in common with {@link #getSpecialPropertyNames()}.
     * <p>
     * Upstream should take extra precautions when dealing with properties
     * named {@code password}.
     * 
     * @return the names of the implementation-specific properties, and their
     * expected type. The ordering is a hint for user interfaces.
     */
    public Collection<String> getPropertyNames();

    /**
     * Special properties are ones which uniquely define the persistent state
     * of the instance. e.g. filenames, usernames, URLs.
     * <p>
     * Upstream will provide file selection dialogs for dealing with properties
     * named {@code filename}.
     * 
     * @return the names of the implementation-specific properties, and their
     * expected type. The ordering is a hint for user interfaces.
     */
    public Collection<String> getSpecialPropertyNames();

    /**
     * @param properties to be used by the implementation.
     */
    public void setProperties(Properties properties);
}
