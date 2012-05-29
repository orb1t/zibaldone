/*
 * Created 08-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

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
     * remain the same across sessions to identify a particular import
     * channel and not clash with other distinct instances.
     * The returned string must not be longer than 24 characters.
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
     * @return the names of the implementation-specific properties.
     */
    public Collection<String> getPropertyNames();

    /**
     * @param properties to be used by the implementation.
     */
    public void setProperties(Properties properties);
}
