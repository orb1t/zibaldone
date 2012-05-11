/*
 * Created 08-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

import java.io.IOException;
import java.util.List;

/**
 * API for importing {@link Note} objects from a variety of file formats and
 * sources.
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
         * @return all the {@link Note}s from the input channel.
         * Implementations should not cache this result as the caller expects
         * a fresh reload of the underlying resource.
         * {@code id} values will be ignored.
         * 
         * @throws IOException
         */
        public List<Note> getNotes() throws IOException;
}
