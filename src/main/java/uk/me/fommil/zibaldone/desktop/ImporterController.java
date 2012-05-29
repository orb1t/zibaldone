/*
 * Created 29-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import java.util.List;
import java.util.Properties;
import uk.me.fommil.zibaldone.Importer;

/**
 * Specialist MVC Controller for working with {@link Importer}s.
 * 
 * @author Samuel Halliday
 */
public class ImporterController {

    public List<Class<? extends Importer>> getImplementations() {
        // TODO: implement method
        throw new UnsupportedOperationException("not implemented yet");
    }

    public void doImport(Class<? extends Importer> klass, Properties properties) {
        // TODO: implement method
        throw new UnsupportedOperationException("not implemented yet");
    }
}
